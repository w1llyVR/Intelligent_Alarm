package com.wvr.despertadorinteligente.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class ActivityAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Más adelante registraremos solo actividad general.
        // No leeremos textos, contraseñas ni contenido de otras aplicaciones.
    }

    override fun onInterrupt() {
        // Android llama este método si interrumpe el servicio.
    }
}