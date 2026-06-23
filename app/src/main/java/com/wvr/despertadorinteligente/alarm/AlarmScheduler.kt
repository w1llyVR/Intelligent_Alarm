package com.wvr.despertadorinteligente.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.wvr.despertadorinteligente.receiver.AlarmReceiver
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    fun programarPrueba(hora: Int, minuto: Int): Calendar {
        val ahora = Calendar.getInstance()

        val fechaProgramada = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hora)
            set(Calendar.MINUTE, minuto)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Si la hora elegida ya pasó, se prueba mañana.
            if (!after(ahora)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmContract.REQUEST_CODE_TEST_ALARM,
            Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmContract.ACTION_TEST_ALARM
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                fechaProgramada.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                fechaProgramada.timeInMillis,
                pendingIntent
            )
        }

        return fechaProgramada
    }

    fun cancelarPrueba() {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmContract.REQUEST_CODE_TEST_ALARM,
            Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmContract.ACTION_TEST_ALARM
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}