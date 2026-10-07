package com.hormi.hormiapp.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hormi.hormiapp.MainActivity
import com.hormi.hormiapp.R

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra(ReminderScheduler.EXTRA_TIME) ?: "20:00"
        showNotification(context)
        // Deja programado el recordatorio del día siguiente.
        ReminderScheduler.schedule(context, time)
    }

    private fun showNotification(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(CHANNEL_ID, "Recordatorio diario", NotificationManager.IMPORTANCE_DEFAULT)
        manager.createNotificationChannel(channel)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return

        val openApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("HormiApp")
            .setContentText("¿Ya registraste tus gastos de hoy? Hasta los más pequeños cuentan.")
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }

    private companion object {
        const val CHANNEL_ID = "recordatorio_diario"
        const val NOTIFICATION_ID = 2001
    }
}
