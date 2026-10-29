package com.moviebox.app.data.repository

import android.content.Context
import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.moviebox.app.BuildConfig
import com.moviebox.app.security.NhNativeSecurityVault
import java.util.Locale
import java.util.UUID

/**
 * Registers every device that opens the app under /users/{uid} so the Admin Panel ->
 * User Management screen can show Total / Online / Offline users.
 *
 * Regular users never see a login screen, so each install silently gets an anonymous Firebase
 * session (Firebase Console -> Authentication -> Sign-in method -> Anonymous must be Enabled).
 * Security rules already allow  /users/{uid}  to be written by that same uid.
 *
 * A stable per-install deviceId is stored too, so that when the same phone gets a new uid
 * (admin sign-in / sign-out) the admin list still shows ONE entry per device.
 */
object UserPresenceManager {

    private const val PREFS = "nh_user_presence"
    private const val KEY_DEVICE_ID = "device_id"

    private var appContext: Context? = null
    private var auth: FirebaseAuth? = null
    private var db: FirebaseDatabase? = null
    private var started = false
    private var foreground = true
    private var anonymousAttempted = false

    private var currentUid: String? = null
    private var currentRef: DatabaseReference? = null
    private var connectedRef: DatabaseReference? = null
    private var connectedListener: ValueEventListener? = null

    fun start(context: Context) {
        if (started) return
        started = true
        appContext = context.applicationContext
        runCatching {
            NhNativeSecurityVault.ensureFirebaseInitialized(context)
            auth = FirebaseAuth.getInstance()
            val dbUrl = NhNativeSecurityVault.getDeveloperInfo(context).firebaseDatabaseUrl
            db = if (dbUrl.isNotBlank()) FirebaseDatabase.getInstance(dbUrl) else FirebaseDatabase.getInstance()
        }
        val firebaseAuth = auth ?: return
        firebaseAuth.addAuthStateListener { fa ->
            val user = fa.currentUser
            if (user == null) {
                detachPresence()
                signInAnonymouslyOnce(fa)
            } else {
                attachPresence(user)
            }
        }
    }

    /** Called from MainActivity.onStart / onStop. */
    fun setForeground(isForeground: Boolean) {
        foreground = isForeground
        val ref = currentRef ?: return
        runCatching {
            ref.updateChildren(
                mapOf(
                    "online" to isForeground,
                    "lastSeen" to ServerValue.TIMESTAMP
                )
            )
        }
    }

    fun updateFcmToken(token: String) {
        if (token.isBlank()) return
        val ref = currentRef ?: return
        runCatching { ref.child("fcmToken").setValue(token) }
    }

    private fun signInAnonymouslyOnce(fa: FirebaseAuth) {
        if (anonymousAttempted) return
        anonymousAttempted = true
        runCatching {
            fa.signInAnonymously().addOnCompleteListener { task ->
                // Allow a retry on the next auth change if it failed (e.g. no internet yet)
                if (!task.isSuccessful) anonymousAttempted = false
            }
        }.onFailure { anonymousAttempted = false }
    }

    private fun attachPresence(user: FirebaseUser) {
        val database = db ?: return
        if (currentUid == user.uid && currentRef != null) return
        detachPresence()
        anonymousAttempted = false

        val ref = database.reference.child("users").child(user.uid)
        currentUid = user.uid
        currentRef = ref

        val email = user.email.orEmpty()
        val displayName = when {
            !user.displayName.isNullOrBlank() -> user.displayName!!
            email.isNotBlank() -> email.substringBefore('@')
            else -> "Guest • ${deviceLabel()}"
        }

        val conn = database.getReference(".info/connected")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.getValue(Boolean::class.java) != true) return
                runCatching {
                    // When the socket drops / app is killed the server flips this user to offline
                    ref.child("online").onDisconnect().setValue(false)
                    ref.child("lastSeen").onDisconnect().setValue(ServerValue.TIMESTAMP)
                    ref.updateChildren(
                        mapOf(
                            "uid" to user.uid,
                            "displayName" to displayName,
                            "email" to email,
                            "deviceId" to deviceId(),
                            "deviceModel" to deviceLabel(),
                            "appVersion" to BuildConfig.VERSION_NAME,
                            "isAnonymous" to user.isAnonymous,
                            "online" to foreground,
                            "lastSeen" to ServerValue.TIMESTAMP
                        )
                    )
                }
                fetchAndStoreFcmToken(ref)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        connectedRef = conn
        connectedListener = listener
        conn.addValueEventListener(listener)
    }

    private fun detachPresence() {
        runCatching {
            connectedListener?.let { connectedRef?.removeEventListener(it) }
        }
        connectedListener = null
        connectedRef = null
        currentRef = null
        currentUid = null
    }

    private fun fetchAndStoreFcmToken(ref: DatabaseReference) {
        runCatching {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                if (!token.isNullOrBlank()) {
                    runCatching { ref.child("fcmToken").setValue(token) }
                }
            }
        }
    }

    private fun deviceId(): String {
        val ctx = appContext ?: return "unknown"
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        if (!existing.isNullOrBlank()) return existing
        val fresh = UUID.randomUUID().toString()
        prefs.edit().putString(KEY_DEVICE_ID, fresh).apply()
        return fresh
    }

    private fun deviceLabel(): String {
        val maker = Build.MANUFACTURER.orEmpty().replaceFirstChar { it.titlecase(Locale.US) }
        val model = Build.MODEL.orEmpty()
        return if (model.startsWith(maker, ignoreCase = true)) model else "$maker $model".trim()
    }
}
