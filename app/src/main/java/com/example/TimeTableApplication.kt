package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.example.data.db.AppDatabase
import com.example.data.preferences.SettingsPreferences
import com.example.data.repository.LectureRepository

class TimeTableApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: LectureRepository
        private set

    lateinit var preferences: SettingsPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        repository = LectureRepository(database.lectureDao())
        preferences = SettingsPreferences(this)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = CHANNEL_ID_LECTURE_ALARMS
            val name = "Lecture Alarms"
            val descriptionText = "High priority full-screen alarms for scheduled timetable lectures"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1500)
                setBypassDnd(true)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
                setSound(alarmSound, audioAttributes)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_LECTURE_ALARMS = "lecture_alarms_channel"
        lateinit var instance: TimeTableApplication
            private set
    }
}
