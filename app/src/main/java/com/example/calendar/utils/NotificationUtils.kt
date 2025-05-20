package com.example.calendar.utils

import android.content.Context
import android.app.NotificationChannel
import android.app.NotificationManager

class NotificationUtils(private val context: Context) {

    fun createNotificationChannel() {
        // Verifique se a versão do Android é 8.0 (Oreo) ou superior
        val name = "Canal Principal de Lembretes" // Nome visível para o usuário nas configurações
        val descriptionText = "Lembretes do calendario" // Descrição visível para o usuário
        val importance = NotificationManager.IMPORTANCE_HIGH // Nível de importância (HIGH, DEFAULT, LOW, MIN)
        val channel = NotificationChannel("task_reminder_channel", name, importance).apply {
            description = descriptionText
             enableVibration(true)
            // Opcional: configurar som, vibração, luzes, etc.
            // setSound(null, null) // Para desativar som
        }

        // Registrar o canal no sistema
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

}