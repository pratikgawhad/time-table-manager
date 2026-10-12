package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.TimeTableApplication
import com.example.alarm.AlarmScheduler
import com.example.data.model.Lecture
import com.example.data.preferences.SettingsPreferences
import com.example.data.repository.LectureRepository
import com.example.ui.FullScreenAlertActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TimetableViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as TimeTableApplication
    private val repository: LectureRepository = app.repository
    val preferences: SettingsPreferences = app.preferences

    private val _selectedDayFilter = MutableStateFlow("All")
    val selectedDayFilter: StateFlow<String> = _selectedDayFilter.asStateFlow()

    val allLectures: StateFlow<List<Lecture>> = repository.allLectures.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val lectures: StateFlow<List<Lecture>> = combine(
        repository.allLectures,
        _selectedDayFilter
    ) { all, filter ->
        if (filter.equals("All", ignoreCase = true)) {
            all
        } else {
            all.filter { lecture ->
                lecture.repeatType == "Daily" ||
                (lecture.repeatType == "Weekdays" && filter != "Saturday" && filter != "Sunday") ||
                (lecture.repeatType == "Weekends" && (filter == "Saturday" || filter == "Sunday")) ||
                lecture.day.contains(filter, ignoreCase = true) ||
                lecture.customDays.contains(filter.take(3), ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setDayFilter(day: String) {
        _selectedDayFilter.value = day
    }

    fun addOrUpdateLecture(
        id: Long = 0L,
        subject: String,
        hour: Int,
        minute: Int,
        day: String,
        room: String,
        colorHex: Long,
        categoryEmoji: String = "📖",
        repeatType: String = "Once",
        customDays: String = ""
    ) {
        viewModelScope.launch {
            val lecture = Lecture(
                id = id,
                subject = subject,
                hour = hour,
                minute = minute,
                day = day,
                enabled = true,
                roomName = room,
                colorHex = colorHex,
                categoryEmoji = categoryEmoji,
                repeatType = repeatType,
                customDays = customDays
            )

            val newId = if (id == 0L) {
                repository.insertLecture(lecture)
            } else {
                repository.updateLecture(lecture)
                id
            }

            val savedLecture = lecture.copy(id = newId)
            AlarmScheduler.scheduleLecture(app.applicationContext, savedLecture)
        }
    }

    fun toggleLectureEnabled(lecture: Lecture, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = lecture.copy(enabled = isEnabled)
            repository.updateLecture(updated)
            if (isEnabled) {
                AlarmScheduler.scheduleLecture(app.applicationContext, updated)
            } else {
                AlarmScheduler.cancelLecture(app.applicationContext, updated.id)
            }
        }
    }

    fun deleteLecture(lecture: Lecture) {
        viewModelScope.launch {
            AlarmScheduler.cancelLecture(app.applicationContext, lecture.id)
            repository.deleteLecture(lecture)
        }
    }

    fun triggerTestAlert(lecture: Lecture?) {
        val context = app.applicationContext
        val testSubject = lecture?.subject ?: "Mathematics 101"
        val testTime = lecture?.formattedTime ?: "09:30 AM"
        val testDay = lecture?.day ?: "Daily"
        val testRoom = lecture?.roomName ?: "Hall A"

        val intent = Intent(context, FullScreenAlertActivity::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_LECTURE_ID, lecture?.id ?: 101L)
            putExtra(AlarmScheduler.EXTRA_SUBJECT, testSubject)
            putExtra(AlarmScheduler.EXTRA_TIME, testTime)
            putExtra(AlarmScheduler.EXTRA_DAY, testDay)
            putExtra(AlarmScheduler.EXTRA_ROOM, testRoom)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }
        context.startActivity(intent)
    }

    // "Load Sample Data" option in Settings for users who want sample data
    fun loadSampleData() {
        viewModelScope.launch {
            val samples = listOf(
                Lecture(
                    subject = "Computer Networks & Security",
                    hour = 9,
                    minute = 0,
                    day = "Monday",
                    enabled = true,
                    roomName = "Lab 4",
                    colorHex = 0xFF6366F1,
                    categoryEmoji = "💻",
                    repeatType = "Weekdays"
                ),
                Lecture(
                    subject = "Data Structures & Algorithms",
                    hour = 11,
                    minute = 30,
                    day = "Monday",
                    enabled = true,
                    roomName = "Hall 201",
                    colorHex = 0xFFA855F7,
                    categoryEmoji = "📚",
                    repeatType = "Daily"
                ),
                Lecture(
                    subject = "Chemical Synthesis & Lab",
                    hour = 14,
                    minute = 15,
                    day = "Daily",
                    enabled = true,
                    roomName = "Lab 303",
                    colorHex = 0xFF22C55E,
                    categoryEmoji = "🧪",
                    repeatType = "Daily"
                ),
                Lecture(
                    subject = "Digital Design & UI Arts",
                    hour = 16,
                    minute = 0,
                    day = "Wednesday",
                    enabled = true,
                    roomName = "Studio B",
                    colorHex = 0xFFEC4899,
                    categoryEmoji = "🎨",
                    repeatType = "Custom",
                    customDays = "Mon, Wed, Fri"
                )
            )
            for (s in samples) {
                val id = repository.insertLecture(s)
                AlarmScheduler.scheduleLecture(app.applicationContext, s.copy(id = id))
            }
        }
    }
}
