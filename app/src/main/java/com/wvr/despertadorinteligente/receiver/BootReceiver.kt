package com.wvr.despertadorinteligente.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Aquí reprogramaremos la alarma después de reiniciar el teléfono.
    }
}