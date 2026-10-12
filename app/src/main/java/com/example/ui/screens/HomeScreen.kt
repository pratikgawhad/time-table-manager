package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture
import com.example.ui.components.LectureCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val FILTER_DAYS = listOf("All", "Daily", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun HomeScreen(
    lectures: List<Lecture>,
    selectedDayFilter: String,
    onSelectDayFilter: (String) -> Unit,
    onAddNewLecture: () -> Unit,
    onEditLecture: (Lecture) -> Unit,
    onDeleteLecture: (Lecture) -> Unit,
    onToggleLectureEnabled: (Lecture, Boolean) -> Unit,
    onTestAlert: (Lecture?) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPermissions: () -> Unit,
    onLoadSampleData: (() -> Unit)? = null,
    hasMissingPermissions: Boolean
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        currentHour in 5..11 -> "Good Morning ☀️"
        currentHour in 12..16 -> "Good Afternoon 🌤️"
        else -> "Good Evening 🌙"
    }

    val todayCalendar = Calendar.getInstance()
    val todayName = when (todayCalendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Monday"
    }

    val tomorrowCalendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
    val tomorrowName = when (tomorrowCalendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> "Tuesday"
    }

    val dateFormatted = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Filter today's and tomorrow's lectures
    val todayLectures = lectures.filter {
        it.repeatType == "Daily" ||
        (it.repeatType == "Weekdays" && todayName != "Saturday" && todayName != "Sunday") ||
        (it.repeatType == "Weekends" && (todayName == "Saturday" || todayName == "Sunday")) ||
        it.day.contains(todayName, ignoreCase = true) ||
        it.customDays.contains(todayName.take(3), ignoreCase = true)
    }

    val tomorrowLectures = lectures.filter {
        it.repeatType == "Daily" ||
        (it.repeatType == "Weekdays" && tomorrowName != "Saturday" && tomorrowName != "Sunday") ||
        (it.repeatType == "Weekends" && (tomorrowName == "Saturday" || tomorrowName == "Sunday")) ||
        it.day.contains(tomorrowName, ignoreCase = true) ||
        it.customDays.contains(tomorrowName.take(3), ignoreCase = true)
    }

    var isTodayExpanded by remember { mutableStateOf(true) }
    var isTomorrowExpanded by remember { mutableStateOf(false) }
    var isAllExpanded by remember { mutableStateOf(true) }

    Scaffold(
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
                        )
                    )
                    .clickable { onAddNewLecture() }
                    .testTag("fab_add_lecture"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Lecture",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            // TOP GRADIENT APP BAR (Indigo -> Purple)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF7C3AED), Color(0xFF8B5CF6))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = greeting,
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Time Table Manager",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .size(42.dp)
                                .testTag("btn_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.16f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateFormatted,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // PERMISSIONS BANNER (If notification or alarm permission missing)
            if (hasMissingPermissions) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
                        .clickable { onOpenPermissions() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = Color(0xFFD97706),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Enable exact alarms & notifications for class alerts.",
                        color = Color(0xFF92400E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Fix",
                        color = Color(0xFFB45309),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // HORIZONTAL DAY FILTER CHIPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FILTER_DAYS.forEach { dayName ->
                    val isSelected = selectedDayFilter.equals(dayName, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF6366F1) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onSelectDayFilter(dayName) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("day_filter_$dayName")
                    ) {
                        Text(
                            text = dayName,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LECTURES LIST & SECTIONS
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // If the entire timetable is clean & empty
                if (lectures.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp)
                                .shadow(2.dp, RoundedCornerShape(24.dp), ambientColor = Color(0x11000000)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEEF2FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color(0xFF6366F1),
                                        modifier = Modifier.size(38.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                Text(
                                    text = "No lectures yet",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "No lectures yet. Tap + to add your first lecture!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(22.dp))

                                Button(
                                    onClick = onAddNewLecture,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6366F1),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add Your First Lecture", fontWeight = FontWeight.Bold)
                                }

                                if (onLoadSampleData != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedButton(
                                        onClick = onLoadSampleData,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.height(40.dp)
                                    ) {
                                        Text("Load Sample Data", fontSize = 13.sp, color = Color(0xFF6366F1))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // SECTION 1: TODAY'S SCHEDULE
                    item {
                        SectionHeaderBar(
                            title = "Today's Schedule ($todayName)",
                            count = todayLectures.size,
                            isExpanded = isTodayExpanded,
                            accentColor = Color(0xFF6366F1),
                            onToggle = { isTodayExpanded = !isTodayExpanded }
                        )
                    }

                    if (isTodayExpanded) {
                        if (todayLectures.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🎉", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "No classes scheduled for today!",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Text(
                                                text = "Enjoy your free day or prepare ahead.",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            items(todayLectures, key = { "today_${it.id}" }) { lecture ->
                                LectureCard(
                                    lecture = lecture,
                                    onToggleEnabled = { isEnabled -> onToggleLectureEnabled(lecture, isEnabled) },
                                    onEdit = { onEditLecture(lecture) },
                                    onDelete = { onDeleteLecture(lecture) },
                                    onTestAlert = { onTestAlert(lecture) }
                                )
                            }
                        }
                    }

                    // SECTION 2: TOMORROW'S PREVIEW
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        SectionHeaderBar(
                            title = "Tomorrow's Schedule ($tomorrowName)",
                            count = tomorrowLectures.size,
                            isExpanded = isTomorrowExpanded,
                            accentColor = Color(0xFF8B5CF6),
                            onToggle = { isTomorrowExpanded = !isTomorrowExpanded }
                        )
                    }

                    if (isTomorrowExpanded) {
                        if (tomorrowLectures.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "No classes scheduled for tomorrow.",
                                        modifier = Modifier.padding(14.dp),
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        } else {
                            items(tomorrowLectures, key = { "tomorrow_${it.id}" }) { lecture ->
                                LectureCard(
                                    lecture = lecture,
                                    onToggleEnabled = { isEnabled -> onToggleLectureEnabled(lecture, isEnabled) },
                                    onEdit = { onEditLecture(lecture) },
                                    onDelete = { onDeleteLecture(lecture) },
                                    onTestAlert = { onTestAlert(lecture) }
                                )
                            }
                        }
                    }

                    // SECTION 3: ALL LECTURES
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        SectionHeaderBar(
                            title = if (selectedDayFilter == "All") "All Lectures" else "$selectedDayFilter Lectures",
                            count = lectures.size,
                            isExpanded = isAllExpanded,
                            accentColor = Color(0xFF10B981),
                            onToggle = { isAllExpanded = !isAllExpanded }
                        )
                    }

                    if (isAllExpanded) {
                        items(lectures, key = { "all_${it.id}" }) { lecture ->
                            LectureCard(
                                lecture = lecture,
                                onToggleEnabled = { isEnabled -> onToggleLectureEnabled(lecture, isEnabled) },
                                onEdit = { onEditLecture(lecture) },
                                onDelete = { onDeleteLecture(lecture) },
                                onTestAlert = { onTestAlert(lecture) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderBar(
    title: String,
    count: Int,
    isExpanded: Boolean,
    accentColor: Color,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onToggle() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
            }
        }

        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(20.dp)
        )
    }
}
