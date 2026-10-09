package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Lecture
import java.util.Locale

// 8 PASTEL ACCENT COLORS
val PASTEL_COLORS = listOf(
    0xFF6366F1, // Indigo
    0xFFA855F7, // Purple
    0xFFEC4899, // Pink
    0xFFEF4444, // Red
    0xFFF97316, // Orange
    0xFFEAB308, // Yellow
    0xFF22C55E, // Green
    0xFF3B82F6  // Blue
)

// 12 SUBJECT EMOJIS
val SUBJECT_EMOJIS = listOf(
    "📚", "📖", "💻", "🧪", "🎨", "🎵",
    "⚽", "🏏", "📝", "🔬", "🌍", "🧮"
)

val REPEAT_OPTIONS = listOf("Daily", "Weekdays", "Weekends", "Custom", "Once")
val DAYS_OF_WEEK = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun AddEditLectureDialog(
    lectureToEdit: Lecture? = null,
    onDismiss: () -> Unit,
    onSave: (
        subject: String,
        hour: Int,
        minute: Int,
        day: String,
        room: String,
        colorHex: Long,
        emoji: String,
        repeatType: String,
        customDays: String
    ) -> Unit
) {
    var subject by remember { mutableStateOf(lectureToEdit?.subject ?: "") }
    var room by remember { mutableStateOf(lectureToEdit?.roomName ?: "") }
    var selectedColor by remember { mutableLongStateOf(lectureToEdit?.colorHex ?: PASTEL_COLORS[0]) }
    var selectedEmoji by remember { mutableStateOf(lectureToEdit?.categoryEmoji ?: "📚") }
    var selectedRepeat by remember { mutableStateOf(lectureToEdit?.repeatType ?: "Daily") }

    val selectedCustomDays = remember {
        mutableStateListOf<String>().apply {
            if (lectureToEdit != null && lectureToEdit.customDays.isNotBlank()) {
                addAll(lectureToEdit.customDays.split(",").map { it.trim() })
            } else {
                addAll(listOf("Mon", "Wed", "Fri"))
            }
        }
    }

    val initial24Hour = lectureToEdit?.hour ?: 9
    val initialMinute = lectureToEdit?.minute ?: 0

    var isAm by remember { mutableStateOf(initial24Hour < 12) }
    var displayHour by remember {
        mutableIntStateOf(
            when {
                initial24Hour == 0 -> 12
                initial24Hour > 12 -> initial24Hour - 12
                else -> initial24Hour
            }
        )
    }
    var minute by remember { mutableIntStateOf(initialMinute) }
    var hasError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (lectureToEdit == null) "Add Lecture" else "Edit Lecture",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Set repeat rule, emoji icon & exact alarm",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 1: SUBJECT
                StepHeader(num = "1", title = "Subject Name")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = subject,
                    onValueChange = {
                        subject = it
                        if (it.isNotBlank()) hasError = false
                    },
                    placeholder = { Text("e.g. Mathematics, Artificial Intelligence") },
                    leadingIcon = {
                        Text(text = selectedEmoji, fontSize = 22.sp, modifier = Modifier.padding(start = 12.dp, end = 4.dp))
                    },
                    isError = hasError,
                    supportingText = {
                        if (hasError) Text("Please enter a subject name", color = Color(0xFFEF4444))
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_subject_name")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 2: SUBJECT ICON (12 EMOJI GRID)
                StepHeader(num = "2", title = "Subject Icon (Choose Emoji)")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SUBJECT_EMOJIS.forEach { emoji ->
                        val isSelected = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 3: COLOR TAG (8 PASTEL CIRCLES)
                StepHeader(num = "3", title = "Card Color Accent")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PASTEL_COLORS.forEach { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(hex))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 4: REPEAT OPTIONS
                StepHeader(num = "4", title = "Repeat Schedule")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    REPEAT_OPTIONS.forEach { opt ->
                        val isSelected = selectedRepeat == opt
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                .clickable { selectedRepeat = opt }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) Color.White else Color(0xFF475569),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // If Custom Days is chosen, show multi-select day chips
                if (selectedRepeat == "Custom") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Select custom repeat days:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DAYS_OF_WEEK.forEach { day ->
                            val isSelected = selectedCustomDays.contains(day)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0xFF8B5CF6) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (isSelected) selectedCustomDays.remove(day)
                                        else selectedCustomDays.add(day)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = day,
                                    color = if (isSelected) Color.White else Color(0xFF475569),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 5: START TIME PICKER
                StepHeader(num = "5", title = "Start Time")
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TimeStepper(value = displayHour, range = 1..12, onValueChange = { displayHour = it }, label = "HOUR")
                        Text(text = ":", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), modifier = Modifier.padding(horizontal = 12.dp))
                        TimeStepper(value = minute, range = 0..59, step = 5, formatTwoDigits = true, onValueChange = { minute = it }, label = "MIN")

                        Spacer(modifier = Modifier.width(20.dp))

                        Column {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isAm) Color(0xFF6366F1) else Color(0xFFE2E8F0))
                                    .clickable { isAm = true }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(text = "AM", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isAm) Color.White else Color(0xFF64748B))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (!isAm) Color(0xFF6366F1) else Color(0xFFE2E8F0))
                                    .clickable { isAm = false }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(text = "PM", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (!isAm) Color.White else Color(0xFF64748B))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 6: ROOM (OPTIONAL)
                StepHeader(num = "6", title = "Room Number (Optional)")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    placeholder = { Text("e.g. Hall 302, Lab-4") },
                    leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF64748B)) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6366F1),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("input_room_name")
                )

                Spacer(modifier = Modifier.height(26.dp))

                // SAVE BUTTON
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                            )
                        )
                        .clickable {
                            if (subject.trim().isBlank()) {
                                hasError = true
                                return@clickable
                            }
                            val final24Hour = when {
                                isAm && displayHour == 12 -> 0
                                !isAm && displayHour < 12 -> displayHour + 12
                                else -> displayHour
                            }
                            val dayLabel = when (selectedRepeat) {
                                "Daily" -> "Daily"
                                "Weekdays" -> "Weekdays"
                                "Weekends" -> "Weekends"
                                "Custom" -> if (selectedCustomDays.isNotEmpty()) selectedCustomDays.joinToString(", ") else "Daily"
                                "Once" -> "One-time"
                                else -> selectedRepeat
                            }
                            onSave(
                                subject.trim(),
                                final24Hour,
                                minute,
                                dayLabel,
                                room.trim(),
                                selectedColor,
                                selectedEmoji,
                                selectedRepeat,
                                selectedCustomDays.joinToString(", ")
                            )
                        }
                        .testTag("save_lecture_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (lectureToEdit == null) "Save to Timetable" else "Update Lecture",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StepHeader(num: String, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFF6366F1).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = num, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
    }
}

@Composable
private fun TimeStepper(
    value: Int,
    range: IntRange,
    step: Int = 1,
    formatTwoDigits: Boolean = false,
    onValueChange: (Int) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        TextButton(
            onClick = {
                val next = if (value + step > range.last) range.first else value + step
                onValueChange(next)
            },
            modifier = Modifier.size(28.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Text("▲", fontSize = 11.sp, color = Color(0xFF6366F1))
        }

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            val text = if (formatTwoDigits) String.format(Locale.getDefault(), "%02d", value) else "$value"
            Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }

        TextButton(
            onClick = {
                val next = if (value - step < range.first) range.last else value - step
                onValueChange(next)
            },
            modifier = Modifier.size(28.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Text("▼", fontSize = 11.sp, color = Color(0xFF6366F1))
        }
    }
}
