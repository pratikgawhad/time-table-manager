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

    private val _showInterstitialAd = MutableStateFlow(false)
    val showInterstitialAd: StateFlow<Boolean> = _showInterstitialAd.asStateFlow()

    val lectures: StateFlow<List<Lecture>> = combine(
        repository.allLectures,
        _selectedDayFilter
    ) { all, filter ->
        if (filter.equals("All", ignoreCase = true)) {
            all
        } else if (filter.equals("Daily", ignoreCase = true)) {
            all.filter { it.repeatType.equals("Daily", ignoreCase = true) || it.day.equals("Daily", ignoreCase = true) }
        } else {
            all.filter {
                it.repeatType.equals("Daily", ignoreCase = true) ||
                it.day.contains(filter, ignoreCase = true) ||
                it.customDays.contains(filter.take(3), ignoreCase = true)
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
        id: Long = 0,
        subject: String,
        hour: Int,
        minute: Int,
        day: String,
        room: String,
        colorHex: Long,
        categoryEmoji: String = "📚",
        repeatType: String = "Daily",
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

            val insertedId = if (id == 0L) {
                repository.insertLecture(lecture)
            } else {
                repository.updateLecture(lecture)
                id
            }

            val savedLecture = lecture.copy(id = insertedId)
            AlarmScheduler.scheduleLecture(app.applicationContext, savedLecture)

            if (preferences.incrementAndCheckInterstitial()) {
                _showInterstitialAd.value = true
            }
        }
    }

    fun toggleLectureEnabled(lecture: Lecture, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = lecture.copy(enabled = isEnabled)
            repository.updateLecture(updated)
            if (isEnabled) {
                AlarmScheduler.scheduleLecture(app.applicationContext, updated)
            } else {
                AlarmScheduler.cancelLecture(app.applicationContext, lecture.id)
            }
        }
    }

    fun deleteLecture(lecture: Lecture) {
        viewModelScope.launch {
            AlarmScheduler.cancelLecture(app.applicationContext, lecture.id)
            repository.deleteLecture(lecture)
        }
    }

    fun dismissInterstitialAd() {
        _showInterstitialAd.value = false
    }

    fun triggerTestAlert(lecture: Lecture?) {
        val context = app.applicationContext
        val testSubject = lecture?.subject ?: "Advanced Mathematics 101"
        val testTime = lecture?.formattedTime ?: "09:30 AM"
        val testDay = lecture?.day ?: "Daily"
        val testRoom = lecture?.roomName ?: "Auditorium A"

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

    init {
        viewModelScope.launch {
            val existing = repository.getEnabledLectures()
            if (existing.isEmpty()) {
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
}
