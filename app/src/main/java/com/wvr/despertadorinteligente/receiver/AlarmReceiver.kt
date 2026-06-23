package com.wvr.despertadorinteligente.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.wvr.despertadorinteligente.alarm.AlarmContract
import com.wvr.despertadorinteligente.service.SmartAlarmService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            AlarmContract.ACTION_TEST_ALARM -> {
                val serviceIntent = Intent(
                    context,
                    SmartAlarmService::class.java
                ).apply {
                    action = AlarmContract.ACTION_START_TEST_SERVICE
                }

                ContextCompat.startForegroundService(context, serviceIntent)
            }
        }
    }
}