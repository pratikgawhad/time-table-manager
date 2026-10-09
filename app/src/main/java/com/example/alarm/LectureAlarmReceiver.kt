package com.example.alarm

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.TimeTableApplication
import com.example.data.preferences.SettingsPreferences
import com.example.ui.FullScreenAlertActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LectureAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = SettingsPreferences(context)
        if (!prefs.areAlarmsGloballyEnabled) {
            Log.d(TAG, "Alarm triggered but global alarms are disabled in settings.")
            return
        }

        val lectureId = intent.getLongExtra(AlarmScheduler.EXTRA_LECTURE_ID, -1L)
        val subject = intent.getStringExtra(AlarmScheduler.EXTRA_SUBJECT) ?: "Upcoming Lecture"
        val time = intent.getStringExtra(AlarmScheduler.EXTRA_TIME) ?: ""
        val day = intent.getStringExtra(AlarmScheduler.EXTRA_DAY) ?: ""
        val room = intent.getStringExtra(AlarmScheduler.EXTRA_ROOM) ?: ""

        Log.d(TAG, "Alarm received for subject: $subject, lectureId: $lectureId")

        // 1. Prepare intent for FullScreenAlertActivity
        val alertIntent = Intent(context, FullScreenAlertActivity::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_LECTURE_ID, lectureId)
            putExtra(AlarmScheduler.EXTRA_SUBJECT, subject)
            putExtra(AlarmScheduler.EXTRA_TIME, time)
            putExtra(AlarmScheduler.EXTRA_DAY, day)
            putExtra(AlarmScheduler.EXTRA_ROOM, room)
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            )
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            (lectureId * 10).toInt(),
            alertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Build high priority notification with full-screen intent
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val contentSubtitle = if (room.isNotBlank()) "$time • $room ($day)" else "$time • $day"

        val notificationBuilder = NotificationCompat.Builder(context, TimeTableApplication.CHANNEL_ID_LECTURE_ALARMS)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Lecture Starting Now!")
            .setContentText("$subject ($contentSubtitle)")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setOngoing(false)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)

        if (prefs.isSoundEnabled) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            notificationBuilder.setSound(alarmSound)
        }

        if (prefs.isVibrationEnabled) {
            notificationBuilder.setVibrate(longArrayOf(0, 1000, 500, 1500))
        }

        val notificationId = (lectureId.toInt()).takeIf { it != -1 } ?: 1001
        notificationManager.notify(notificationId, notificationBuilder.build())

        // 3. Attempt direct activity launch as well
        try {
            context.startActivity(alertIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Direct startActivity from receiver failed (notification fallback active): ${e.message}")
        }

        // 4. Re-arm the next recurrence in the database if lecture still exists and is enabled
        if (lectureId != -1L) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as? TimeTableApplication
                    val repo = app?.repository
                    val lecture = repo?.getLectureById(lectureId)
                    if (lecture != null && lecture.enabled) {
                        AlarmScheduler.scheduleLecture(context, lecture)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling next occurrence: ${e.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "LectureAlarmReceiver"
    }
}
