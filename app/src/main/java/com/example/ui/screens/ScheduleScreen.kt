package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture
import com.example.ui.components.LectureCard
import java.util.Calendar

private val WEEK_DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

@Composable
fun ScheduleScreen(
    lectures: List<Lecture>,
    onEditLecture: (Lecture) -> Unit,
    onDeleteLecture: (Lecture) -> Unit,
    onToggleLectureEnabled: (Lecture, Boolean) -> Unit,
    onTestAlert: (Lecture?) -> Unit
) {
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

    var selectedDay by remember { mutableStateOf(todayName) }

    val dayLectures = lectures.filter {
        it.repeatType == "Daily" ||
        (it.repeatType == "Weekdays" && selectedDay != "Saturday" && selectedDay != "Sunday") ||
        (it.repeatType == "Weekends" && (selectedDay == "Saturday" || selectedDay == "Sunday")) ||
        it.day.contains(selectedDay, ignoreCase = true) ||
        it.customDays.contains(selectedDay.take(3), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .shadow(2.dp, ambientColor = Color(0x0D000000))
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Weekly Schedule",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Browse timetable day-by-day",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HORIZONTAL DAY SELECTION TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WEEK_DAYS.forEach { dayName ->
                val isSelected = selectedDay.equals(dayName, ignoreCase = true)
                val isToday = todayName.equals(dayName, ignoreCase = true)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                isSelected -> Color(0xFF6366F1)
                                isToday -> Color(0xFFEEF2FF)
                                else -> Color.White
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                isSelected -> Color(0xFF6366F1)
                                isToday -> Color(0xFFC7D2FE)
                                else -> Color(0xFFE2E8F0)
                            },
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedDay = dayName }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("weekly_day_$dayName")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayName.take(3).uppercase(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isSelected -> Color.White
                                isToday -> Color(0xFF4338CA)
                                else -> Color(0xFF475569)
                            }
                        )
                        if (isToday) {
                            Text(
                                text = "Today",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color(0xFF6366F1)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // LECTURES LIST
        if (dayLectures.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📅", fontSize = 42.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No classes on $selectedDay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add a lecture or switch to another day to review your schedule.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(dayLectures, key = { "sched_${it.id}" }) { lecture ->
                    LectureCard(
                        lecture = lecture,
                        onToggleEnabled = { isEnabled -> onToggleLectureEnabled(lecture, isEnabled) },
                        onEdit = { onEditLecture(lecture) },
                        onDelete = { onDeleteLecture(lecture) },
                        onTestAlert = { onTestAlert(lecture) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }
}
