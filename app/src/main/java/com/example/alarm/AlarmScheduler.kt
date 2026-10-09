package com.example.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.MainActivity
import com.example.data.model.Lecture
import java.util.Calendar

object AlarmScheduler {

    private const val TAG = "AlarmScheduler"
    const val EXTRA_LECTURE_ID = "extra_lecture_id"
    const val EXTRA_SUBJECT = "extra_subject"
    const val EXTRA_TIME = "extra_time"
    const val EXTRA_DAY = "extra_day"
    const val EXTRA_ROOM = "extra_room"

    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.canScheduleExactAlarms() ?: true
        } else {
            true
        }
    }

    fun scheduleLecture(context: Context, lecture: Lecture) {
        if (!lecture.enabled) {
            cancelLecture(context, lecture.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerTime = calculateNextTriggerTime(lecture)

        val intent = Intent(context, LectureAlarmReceiver::class.java).apply {
            putExtra(EXTRA_LECTURE_ID, lecture.id)
            putExtra(EXTRA_SUBJECT, lecture.subject)
            putExtra(EXTRA_TIME, lecture.formattedTime)
            putExtra(EXTRA_DAY, lecture.day)
            putExtra(EXTRA_ROOM, lecture.roomName)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            lecture.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val showIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d(TAG, "Scheduled alarm for '${lecture.subject}' at $triggerTime")
        } catch (e: SecurityException) {
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (ex: Exception) {
                Log.e(TAG, "Failed fallback alarm: ${ex.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scheduling alarm: ${e.message}")
        }
    }

    fun scheduleSnooze(context: Context, lectureId: Long, subject: String, minutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val triggerTime = System.currentTimeMillis() + (minutes * 60 * 1000L)

        val intent = Intent(context, LectureAlarmReceiver::class.java).apply {
            putExtra(EXTRA_LECTURE_ID, lectureId)
            putExtra(EXTRA_SUBJECT, "$subject (Snoozed $minutes m)")
            putExtra(EXTRA_TIME, "Snooze Alert")
            putExtra(EXTRA_DAY, "Now")
            putExtra(EXTRA_ROOM, "")
        }

        val snoozeRequestCode = (lectureId + 99999).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            snoozeRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            val showIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(triggerTime, showIntent), pendingIntent)
        } catch (e: Exception) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    fun cancelLecture(context: Context, lectureId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LectureAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            lectureId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun calculateNextTriggerTime(lecture: Lecture): Long {
        val hour = lecture.hour
        val minute = lecture.minute
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val now = System.currentTimeMillis()

        when (lecture.repeatType) {
            "Daily" -> {
                if (calendar.timeInMillis <= now) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }
            "Weekdays" -> {
                while (calendar.timeInMillis <= now ||
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                ) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }
            "Weekends" -> {
                while (calendar.timeInMillis <= now ||
                    (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                     calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                ) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }
            "Once" -> {
                return if (lecture.oneTimeDateMillis > 0) lecture.oneTimeDateMillis
                else calendar.timeInMillis
            }
            else -> {
                // Specific day of week or custom
                val targetDayOfWeek = when (lecture.day.trim().lowercase()) {
                    "sunday" -> Calendar.SUNDAY
                    "monday" -> Calendar.MONDAY
                    "tuesday" -> Calendar.TUESDAY
                    "wednesday" -> Calendar.WEDNESDAY
                    "thursday" -> Calendar.THURSDAY
                    "friday" -> Calendar.FRIDAY
                    "saturday" -> Calendar.SATURDAY
                    else -> Calendar.MONDAY
                }
                while (calendar.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek || calendar.timeInMillis <= now) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                return calendar.timeInMillis
            }
        }
    }
}
