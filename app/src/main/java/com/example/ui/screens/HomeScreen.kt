package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.components.AdMobBannerCard
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
                    .shadow(12.dp, CircleShape, ambientColor = Color(0x336366F1), spotColor = Color(0x666366F1))
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
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
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            AdMobBannerCard()
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TOP GRADIENT APPBAR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = greeting,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dateFormatted,
                            color = Color(0xFFE0E7FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { onTestAlert(lectures.firstOrNull()) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("home_test_alert_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Test Alert",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (hasMissingPermissions) {
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onOpenPermissions,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.3f))
                                    .testTag("home_permissions_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Permissions",
                                    tint = Color(0xFFFEF3C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("home_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // HORIZONTAL SCROLL OF DAY CHIPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FILTER_DAYS.forEach { day ->
                    val isSelected = selectedDayFilter.equals(day, true)
                    Box(
                        modifier = Modifier
                            .shadow(
                                elevation = if (isSelected) 4.dp else 0.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = Color(0x226366F1),
                                spotColor = Color(0x336366F1)
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF6366F1) else Color.White)
                            .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                            .clickable { onSelectDayFilter(day) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("filter_chip_$day")
                    ) {
                        Text(
                            text = day,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }

            // COLLAPSIBLE SECTIONS LIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // FEATURE 2 - SECTION 1: TODAY'S LECTURES
                item {
                    SectionHeaderBar(
                        title = "Today's Lectures ($todayName)",
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 1.dp,
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF6366F1).copy(alpha = 0.4f), Color(0xFF8B5CF6).copy(alpha = 0.4f))
                                        ),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🎉", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "No lectures today!",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
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

                // FEATURE 2 - SECTION 2: TOMORROW'S PREVIEW
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

                // FEATURE 2 - SECTION 3: ALL LECTURES
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
                    if (lectures.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "📅", fontSize = 40.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No lectures found",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "Tap the '+' button to add your first lecture",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    } else {
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
            .padding(vertical = 6.dp, horizontal = 4.dp),
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "$count",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
        }

        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color(0xFF64748B)
        )
    }
}
