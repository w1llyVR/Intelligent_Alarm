package com.wvr.despertadorinteligente.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wvr.despertadorinteligente.R
import com.wvr.despertadorinteligente.alarm.AlarmScheduler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var tvEstado: TextView
    private lateinit var btnPermisoUso: Button
    private lateinit var btnAccesibilidad: Button
    private lateinit var btnPrueba: Button
    private lateinit var btnProgramar: Button
    private lateinit var btnDesactivarManana: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEstado = findViewById(R.id.tvEstado)
        btnPermisoUso = findViewById(R.id.btnPermisoUso)
        btnAccesibilidad = findViewById(R.id.btnAccesibilidad)
        btnPrueba = findViewById(R.id.btnPrueba)
        btnProgramar = findViewById(R.id.btnProgramar)
        btnDesactivarManana = findViewById(R.id.btnDesactivarManana)

        btnPermisoUso.setOnClickListener {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        btnAccesibilidad.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        btnPrueba.setOnClickListener {
            abrirSelectorHoraPrueba()
        }

        btnProgramar.setOnClickListener {
            tvEstado.text =
                "Estado: la programación de lunes a viernes se implementará después de validar la prueba."
        }

        btnDesactivarManana.setOnClickListener {
            tvEstado.text =
                "Estado: la desactivación temporal se implementará después de validar la prueba."
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarEstado()
    }

    private fun abrirSelectorHoraPrueba() {
        val ahora = Calendar.getInstance()

        TimePickerDialog(
            this,
            { _, horaSeleccionada, minutoSeleccionado ->

                val fechaProgramada = AlarmScheduler(applicationContext)
                    .programarPrueba(
                        horaSeleccionada,
                        minutoSeleccionado
                    )

                val formato = SimpleDateFormat(
                    "EEEE dd/MM/yyyy HH:mm",
                    Locale("es", "PE")
                )

                tvEstado.text =
                    "Prueba programada para: ${formato.format(fechaProgramada.time)}. " +
                            "Bloquea el teléfono y espera la alarma."
            },
            ahora.get(Calendar.HOUR_OF_DAY),
            ahora.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun actualizarEstado() {
        val accessibilityManager =
            getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

        val accesibilidadActiva =
            accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).any {
                it.resolveInfo.serviceInfo.packageName == packageName
            }

        tvEstado.text =
            if (accesibilidadActiva) {
                "Estado: detección de actividad habilitada."
            } else {
                "Estado: activa Acceso de uso y Detección de actividad."
            }
    }
}