package com.example.ui.dialogs

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionsDialog(
    onRequestNotificationPermission: () -> Unit,
    hasNotificationPermission: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val hasExactAlarm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager?.canScheduleExactAlarms() ?: true
    } else {
        true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Required Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Time Table Manager requires these permissions to wake your screen, trigger exact alarms, and vibrate when lectures start.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Exact Alarm Permission
                PermissionItemCard(
                    title = "Exact Alarms (Android 12+)",
                    description = "Needed to fire class alarms at the exact minute even when device is sleeping.",
                    icon = Icons.Default.Alarm,
                    isGranted = hasExactAlarm,
                    actionText = "Open Settings",
                    onAction = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            try {
                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Notification Permission (Android 13+)
                PermissionItemCard(
                    title = "Post Notifications (Android 13+)",
                    description = "Delivers high-priority full-screen wake alerts over lockscreen.",
                    icon = Icons.Default.Notifications,
                    isGranted = hasNotificationPermission,
                    actionText = "Grant",
                    onAction = onRequestNotificationPermission
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Vibration & Wake
                PermissionItemCard(
                    title = "Vibrate & Wake Device",
                    description = "Vibrates for 3 seconds and turns screen on during alarm.",
                    icon = Icons.Default.Vibration,
                    isGranted = true, // Normal manifest permission
                    actionText = null,
                    onAction = {}
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_permissions_dialog")
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
private fun PermissionItemCard(
    title: String,
    description: String,
    icon: ImageVector,
    isGranted: Boolean,
    actionText: String?,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isGranted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (isGranted) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (isGranted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Granted",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Pending",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            if (!isGranted && actionText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onAction,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = actionText, fontSize = 11.sp)
                }
            }
        }
    }
}
