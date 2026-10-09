package com.example.ui.screens

import android.app.AlarmManager
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
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
    onThemeChanged: (String) -> Unit = {}
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    var alarmsEnabled by remember { mutableStateOf(preferences.areAlarmsGloballyEnabled) }
    var vibrationEnabled by remember { mutableStateOf(preferences.isVibrationEnabled) }
    var soundEnabled by remember { mutableStateOf(preferences.isSoundEnabled) }
    var currentThemeMode by remember { mutableStateOf(preferences.themeMode) }

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val hasExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager?.canScheduleExactAlarms() ?: true
    } else {
        true
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "App Settings",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                // SECTION 1: ALARMS & VIBRATION
                GroupHeader(title = "ALARM & WAKE CONTROLS")
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        SettingsRow(
                            title = "Master Alarm Switch",
                            subtitle = "Enable or disable all scheduled wake alarms",
                            icon = Icons.Default.Alarm,
                            iconTint = Color(0xFF6366F1),
                            checked = alarmsEnabled,
                            onCheckedChange = {
                                alarmsEnabled = it
                                preferences.areAlarmsGloballyEnabled = it
                            },
                            tag = "toggle_master_alarms"
                        )

                        SettingsRow(
                            title = "Vibrate for 3 Seconds",
                            subtitle = "Vibrate phone when full-screen alert opens",
                            icon = Icons.Default.Vibration,
                            iconTint = Color(0xFFF59E0B),
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                vibrationEnabled = it
                                preferences.isVibrationEnabled = it
                            },
                            tag = "toggle_vibration"
                        )

                        SettingsRow(
                            title = "Alarm Audio",
                            subtitle = "Play system alert ringtone on lecture time",
                            icon = Icons.Default.VolumeUp,
                            iconTint = Color(0xFF10B981),
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

                // FEATURE 5: THEME MODE SELECTION
                GroupHeader(title = "APPEARANCE & DARK MODE")
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFF8B5CF6))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Theme Preference", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        listOf("light" to "Light Mode (Default)", "dark" to "Dark Mode (Deep Slate)", "system" to "System Default").forEach { (mode, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        currentThemeMode = mode
                                        preferences.themeMode = mode
                                        onThemeChanged(mode)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentThemeMode == mode,
                                    onClick = {
                                        currentThemeMode = mode
                                        preferences.themeMode = mode
                                        onThemeChanged(mode)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = label, fontSize = 14.sp, color = Color(0xFF1E293B))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 3: TEST & PREVIEW
                GroupHeader(title = "DIAGNOSTICS & PREVIEW")
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Test Full-Screen Wake Alert (3s)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1E293B))
                                Text(text = "Includes Snooze 5m/10m & 3s vibration", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onTestAlert,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("trigger_test_alert_button")
                        ) {
                            Text("Launch 3-Second Test Overlay", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 4: EXACT ALARMS
                GroupHeader(title = "PERMISSIONS")
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(20.dp), ambientColor = Color(0x11000000))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Exact Alarm Permission", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
                                Text(
                                    text = if (hasExactAlarm) "Active & Allowed" else "Tap Grant to enable exact alarms",
                                    fontSize = 12.sp,
                                    color = if (hasExactAlarm) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }

                        if (!hasExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    context.startActivity(intent)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Grant", fontSize = 12.sp)
                            }
                        } else {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Active", tint = Color(0xFF10B981), modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // SECTION 5: ABOUT
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
                                Text(text = "Version 2.0.0 • 8-Feature Pro Edition", fontSize = 12.sp, color = Color(0xFF64748B))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Includes Onboarding, Today/Tomorrow Schedule, Snooze, 12 Subject Icons, 8 Pastel Color Accents, Repeat Rules (Daily, Weekdays, Weekends, Custom), Dark Theme, and Home Widget.",
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
}

@Composable
private fun GroupHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6366F1),
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1E293B))
                Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF6366F1),
                uncheckedThumbColor = Color(0xFF94A3B8),
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.testTag(tag)
        )
    }
}
