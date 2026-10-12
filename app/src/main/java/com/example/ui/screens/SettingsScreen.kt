package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.preferences.SettingsPreferences

@Composable
fun SettingsScreen(
    preferences: SettingsPreferences,
    onTestAlert: () -> Unit,
    onNavigateBack: () -> Unit,
    onLoadSampleData: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onThemeChanged: (String) -> Unit = {}
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    var alarmsEnabled by remember { mutableStateOf(preferences.areAlarmsGloballyEnabled) }
    var vibrationEnabled by remember { mutableStateOf(preferences.isVibrationEnabled) }
    var soundEnabled by remember { mutableStateOf(preferences.isSoundEnabled) }
    var currentTheme by remember { mutableStateOf(preferences.themeMode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // TOP APP BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .shadow(2.dp, ambientColor = Color(0x0F000000))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("btn_back_settings")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF1E293B)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        // SETTINGS CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // SECTION 1: ALARM & ALERT SETTINGS
            GroupHeader(title = "ALERTS & SOUNDS")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    SettingToggleRow(
                        icon = Icons.Default.Alarm,
                        iconBg = Color(0xFFEEF2FF),
                        iconTint = Color(0xFF6366F1),
                        title = "Enable Lecture Alarms",
                        subtitle = "Trigger notifications and alerts at lecture time",
                        checked = alarmsEnabled,
                        onCheckedChange = {
                            alarmsEnabled = it
                            preferences.areAlarmsGloballyEnabled = it
                        },
                        tag = "toggle_alarms"
                    )

                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFF59E0B),
                        title = "3-Second Vibration",
                        subtitle = "Vibrate phone for 3 seconds when lecture begins",
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            preferences.isVibrationEnabled = it
                        },
                        tag = "toggle_vibration"
                    )

                    SettingToggleRow(
                        icon = Icons.Default.VolumeUp,
                        iconBg = Color(0xFFDCFCE7),
                        iconTint = Color(0xFF10B981),
                        title = "Alarm Sound",
                        subtitle = "Play system alarm ringtone at schedule trigger",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            preferences.isSoundEnabled = it
                        },
                        tag = "toggle_sound"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: TEST ALERT ACTION
            GroupHeader(title = "QUICK TEST")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3E8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Test Full-Screen White Alert",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Opens the pure white full-screen alert and 3-second vibration immediately",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onTestAlert,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_trigger_test_alert")
                    ) {
                        Text(text = "Preview Alert & Vibration", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 3: APPEARANCE
            GroupHeader(title = "APPEARANCE")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Theme Mode",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Light Mode
                        ThemeSelectionChip(
                            icon = Icons.Default.LightMode,
                            label = "Light",
                            isSelected = currentTheme == "light",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentTheme = "light"
                                preferences.themeMode = "light"
                                onThemeChanged("light")
                            }
                        )

                        // Dark Mode
                        ThemeSelectionChip(
                            icon = Icons.Default.DarkMode,
                            label = "Dark",
                            isSelected = currentTheme == "dark",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentTheme = "dark"
                                preferences.themeMode = "dark"
                                onThemeChanged("dark")
                            }
                        )

                        // System Mode
                        ThemeSelectionChip(
                            icon = Icons.Default.Alarm,
                            label = "Auto",
                            isSelected = currentTheme == "system",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                currentTheme = "system"
                                preferences.themeMode = "system"
                                onThemeChanged("system")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 4: SAMPLE DATA LOADER
            GroupHeader(title = "SAMPLE DATA")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Dataset, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Load Sample Lectures", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF1E293B))
                            Text(text = "Populate example courses to explore schedule features", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onLoadSampleData,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("btn_load_sample_data")
                    ) {
                        Text(text = "Load Sample Data", fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 5: POLICIES
            GroupHeader(title = "POLICIES")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenPrivacyPolicy() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Policy, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Privacy Policy", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                Text(text = "Google Play Developer program compliance", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }
                        Text(text = "Read", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6366F1))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 6: ABOUT
            GroupHeader(title = "ABOUT")
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⏰", fontSize = 26.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Time Table Manager", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B))
                            Text(text = "Version 2.1.0", fontSize = 12.sp, color = Color(0xFF64748B))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Includes Timetable Management, Daily & Weekly Schedule, Repeat Rules, 3-Second Vibration, Full-Screen White Alerts, Snooze, Subject Icons & Color Accents, and Dark Theme.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun GroupHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF94A3B8),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFFE2E8F0)
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun ThemeSelectionChip(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF6366F1) else Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF6366F1) else Color(0xFF64748B)
            )
        }
    }
}
