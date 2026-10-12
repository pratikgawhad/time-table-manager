package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object VibrationHelper {

    private const val TAG = "VibrationHelper"

    @Volatile
    private var lastVibrationTimeMs: Long = 0L

    /**
     * Triggers a vibration for the specified duration (default ~3000ms),
     * avoiding duplicate vibrations within the debounce interval (3.5s).
     */
    @Synchronized
    fun vibrate3Seconds(context: Context, force: Boolean = false) {
        val now = System.currentTimeMillis()
        if (!force && (now - lastVibrationTimeMs < 3500)) {
            Log.d(TAG, "Vibration skipped (already vibrating / debounced)")
            return
        }
        lastVibrationTimeMs = now

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                }
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(3000)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error executing vibration: ${e.message}")
        }
    }

    fun cancel(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.cancel()
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.cancel()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling vibration: ${e.message}")
        }
    }
}
