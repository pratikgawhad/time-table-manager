package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val hour: Int, // 0 to 23
    val minute: Int, // 0 to 59
    val day: String, // "Daily", "Monday", ..., "Sunday", or "Custom"
    val enabled: Boolean = true,
    val roomName: String = "",
    val colorHex: Long = 0xFF6366F1,
    val categoryEmoji: String = "📚",
    val repeatType: String = "Daily", // "Daily", "Weekdays", "Weekends", "Custom", "Once"
    val customDays: String = "", // e.g. "Mon, Wed, Fri"
    val oneTimeDateMillis: Long = 0L
) {
    val formattedTime: String
        get() {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, period)
        }

    val repeatSummary: String
        get() = when (repeatType) {
            "Daily" -> "Every day"
            "Weekdays" -> "Mon - Fri"
            "Weekends" -> "Sat - Sun"
            "Custom" -> if (customDays.isNotBlank()) customDays else day
            "Once" -> "One-time"
            else -> day
        }
}
