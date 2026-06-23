package com.wvr.despertadorinteligente.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.wvr.despertadorinteligente.R
import com.wvr.despertadorinteligente.alarm.AlarmContract
import com.wvr.despertadorinteligente.ui.MainActivity

class SmartAlarmService : Service() {

    companion object {
        private const val CHANNEL_ID = "canal_prueba_alarma"
        private const val NOTIFICATION_ID = 2001
        private const val DURACION_PRUEBA_MS = 30_000L
    }

    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    private val finalizarPruebaRunnable = Runnable {
        detenerPruebaYServicio()
    }

    override fun onCreate() {
        super.onCreate()

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        crearCanalNotificacion()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {

            AlarmContract.ACTION_START_TEST_SERVICE -> {
                startForeground(
                    NOTIFICATION_ID,
                    crearNotificacionPrueba()
                )

                iniciarPrueba()
            }
        }

        return START_NOT_STICKY
    }

    private fun iniciarPrueba() {
        handler.removeCallbacks(finalizarPruebaRunnable)

        detenerAudioYVibracion()

        try {
            val alarmUri =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

            if (alarmUri != null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION
                            )
                            .build()
                    )

                    setDataSource(this@SmartAlarmService, alarmUri)
                    isLooping = true
                    prepare()
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        iniciarVibracionPrueba()

        handler.postDelayed(
            finalizarPruebaRunnable,
            DURACION_PRUEBA_MS
        )
    }

    private fun iniciarVibracionPrueba() {
        val patron = longArrayOf(
            0L,
            350L,
            650L
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(patron, 0)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(patron, 0)
        }
    }

    private fun detenerAudioYVibracion() {
        handler.removeCallbacks(finalizarPruebaRunnable)

        mediaPlayer?.let { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
            } catch (_: Exception) {
            }

            player.release()
        }

        mediaPlayer = null
        vibrator?.cancel()
    }

    private fun detenerPruebaYServicio() {
        detenerAudioYVibracion()

        stopForeground(true)
        stopSelf()
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Prueba de alarma",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description =
                "Notificación visible mientras se prueba la alarma."
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun crearNotificacionPrueba() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Prueba de alarma activa")
            .setContentText(
                "La prueba sonará y vibrará durante 30 segundos."
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setContentIntent(crearPendingIntentAbrirApp())
            .build()

    private fun crearPendingIntentAbrirApp(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroy() {
        detenerAudioYVibracion()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}