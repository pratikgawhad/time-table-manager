package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture

@Composable
fun LectureCard(
    lecture: Lecture,
    onToggleEnabled: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTestAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(lecture.colorHex)
    val animatedBg by animateColorAsState(
        targetValue = if (lecture.enabled) Color.White else Color(0xFFF8FAFC),
        animationSpec = tween(300),
        label = "cardBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (lecture.enabled) 4.dp else 1.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = Color(0x1A000000),
                spotColor = Color(0x1F6366F1)
            )
            .testTag("lecture_card_${lecture.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = animatedBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // LEFT COLORED ACCENT BAR (5px wide)
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(
                        if (lecture.enabled) categoryColor else Color(0xFFCBD5E1)
                    )
            )

            // MAIN CARD CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // TOP ROW: Emoji + Time Pill Badge + Day Label + Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Category Emoji Avatar Box
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(categoryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lecture.categoryEmoji,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Time Pill Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (lecture.enabled) categoryColor.copy(alpha = 0.15f)
                                    else Color(0xFFE2E8F0)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = lecture.formattedTime,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (lecture.enabled) categoryColor else Color(0xFF64748B)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Day Tag
                        Text(
                            text = lecture.day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }

                    // Modern Switch
                    Switch(
                        checked = lecture.enabled,
                        onCheckedChange = onToggleEnabled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6366F1),
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFFE2E8F0),
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .testTag("switch_lecture_${lecture.id}")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // SUBJECT NAME: Bold, 18-20sp, Dark Slate (#1E293B)
                Text(
                    text = lecture.subject,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (lecture.enabled) Color(0xFF1E293B) else Color(0xFF94A3B8),
                    lineHeight = 24.sp
                )

                // Optional Room with location icon
                if (lecture.roomName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Room ${lecture.roomName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BOTTOM ACTION ROW: "Test Alert" text button + Edit & Delete icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onTestAlert,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF6366F1),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Test 3s Alert",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6366F1)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .testTag("edit_lecture_${lecture.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .testTag("delete_lecture_${lecture.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
