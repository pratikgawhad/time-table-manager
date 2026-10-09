package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.TimeTableApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "BootReceiver received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            val app = context.applicationContext as? TimeTableApplication ?: return
            val repo = app.repository
            val prefs = app.preferences

            if (!prefs.areAlarmsGloballyEnabled) {
                Log.d(TAG, "Global alarms are disabled. Skipping rescheduling on boot.")
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val enabledLectures = repo.getEnabledLectures()
                    Log.d(TAG, "Rescheduling ${enabledLectures.size} active lectures after reboot/time change.")
                    for (lecture in enabledLectures) {
                        AlarmScheduler.scheduleLecture(context, lecture)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to reschedule lectures: ${e.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
