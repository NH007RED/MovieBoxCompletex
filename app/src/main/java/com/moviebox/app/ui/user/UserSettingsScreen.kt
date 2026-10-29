package com.moviebox.app.ui.user

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.VerifiedUser
import com.moviebox.app.BuildConfig
import com.moviebox.app.security.NhNativeSecurityVault
import com.moviebox.app.ui.theme.StatusGreen
import com.moviebox.app.ui.theme.CrimsonLight

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DownloadForOffline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moviebox.app.R
import com.moviebox.app.data.model.AppPreferences
import com.moviebox.app.data.model.ThemeVariant
import com.moviebox.app.ui.theme.CrimsonRed
import com.moviebox.app.ui.theme.TextSecondary
import com.moviebox.app.ui.theme.TextWhite
import java.util.Locale

@Composable
fun UserSettingsScreen(
    preferences: AppPreferences,
    cacheSizeBytes: Long,
    onSelectTheme: (ThemeVariant) -> Unit,
    onSelectQuality: (String) -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onClearCache: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenOfflineDownloads: () -> Unit = {}
) {
    var qualityMenu by remember { mutableStateOf(false) }
    val qualities = listOf("Auto", "HD (1080p)", "HD (720p)", "SD (480p)")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings & Profile",
            style = MaterialTheme.typography.headlineSmall,
            color = TextWhite,
            fontWeight = FontWeight.Bold
        )

        // Quick Access to Offline Downloads
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, CrimsonRed.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                .clickable(onClick = onOpenOfflineDownloads)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DownloadForOffline,
                    contentDescription = null,
                    tint = CrimsonRed,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "My Offline Downloads",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Watch downloaded Movies & Web Series without internet",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            Button(
                onClick = onOpenOfflineDownloads,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Open", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // App Settings Card (matching Screen 13 in 1219435.png)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "App Settings",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            // App Theme (DeepBlack vs DeepRed)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("App Theme", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSelectTheme(ThemeVariant.DEEP_BLACK) }
                    ) {
                        RadioButton(
                            selected = preferences.themeVariant == ThemeVariant.DEEP_BLACK,
                            onClick = { onSelectTheme(ThemeVariant.DEEP_BLACK) },
                            colors = RadioButtonDefaults.colors(selectedColor = CrimsonRed)
                        )
                        Text("Dark", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                    }
                    Spacer(Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onSelectTheme(ThemeVariant.DEEP_RED) }
                    ) {
                        RadioButton(
                            selected = preferences.themeVariant == ThemeVariant.DEEP_RED,
                            onClick = { onSelectTheme(ThemeVariant.DEEP_RED) },
                            colors = RadioButtonDefaults.colors(selectedColor = CrimsonRed)
                        )
                        Text("Red", style = MaterialTheme.typography.bodySmall, color = TextWhite)
                    }
                }
            }

            // Default Video Quality Preference
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Preferred Quality", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                    Text(
                        "Applied when the video source provides multiple tracks",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clickable { qualityMenu = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(preferences.defaultQuality, style = MaterialTheme.typography.bodySmall, color = TextWhite)
                    }
                    DropdownMenu(
                        expanded = qualityMenu,
                        onDismissRequest = { qualityMenu = false }
                    ) {
                        qualities.forEach { q ->
                            DropdownMenuItem(
                                text = { Text(q) },
                                onClick = {
                                    onSelectQuality(q)
                                    qualityMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Auto Play Next Episode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Auto Play Next Episode", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Switch(
                    checked = preferences.autoPlayNextEpisode,
                    onCheckedChange = onToggleAutoPlay,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CrimsonRed
                    )
                )
            }

            // Notifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Notifications", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                Switch(
                    checked = preferences.notificationsEnabled,
                    onCheckedChange = onToggleNotifications,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = CrimsonRed
                    )
                )
            }
        }

        // About & Developer Card — Decrypted at runtime exclusively from Native `libnh_security.so`!
        val context = LocalContext.current
        val nativeDevInfo = remember(context) {
            NhNativeSecurityVault.getDeveloperInfo(context)
        }
        val openTelegramLink: () -> Unit = {
            if (nativeDevInfo.telegramUrl.isNotBlank()) {
                runCatching {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(nativeDevInfo.telegramUrl)).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "About & Developer",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Version", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // Developer Name loaded from Native .so Vault (Clean single-line layout, no green text)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Code,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Text(
                        text = "About Developer",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = nativeDevInfo.developerName,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    softWrap = false
                )
            }

            // Telegram Link loaded from Native .so Vault
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, Color(0xFF29B6F6).copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .clickable(onClick = openTelegramLink)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF29B6F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Telegram",
                            tint = Color(0xFF29B6F6),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Official Telegram Channel",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )
                        Text(
                            text = nativeDevInfo.telegramUrl,
                            color = Color(0xFF29B6F6),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Button(
                    onClick = openTelegramLink,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Join",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Clear Cache", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                    val mb = String.format(Locale.US, "%.1f MB", cacheSizeBytes / (1024.0 * 1024.0))
                    Text(mb, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Button(
                    onClick = onClearCache,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clear", color = Color.White)
                }
            }
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Info, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(R.string.tmdb_attribution),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Admin Panel Entry Card (Protected by Firebase Authentication & /adminRoles/{uid})
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Catalog Administration",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Authorized administrators can sign in with Firebase Authentication to manage categories, movies, web series, and TMDB metadata imports.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Button(
                onClick = onOpenAdminPanel,
                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Open Admin Panel", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
