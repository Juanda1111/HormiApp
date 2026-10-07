package com.hormi.hormiapp.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderScheduler {
    private const val REQUEST_CODE = 1001
    const val EXTRA_TIME = "time"

    private fun pendingIntent(context: Context, time: String): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).putExtra(EXTRA_TIME, time)
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Programa el próximo recordatorio a la hora indicada ("HH:mm"). Se reprograma solo cada día. */
    fun schedule(context: Context, time: String) {
        val (hour, minute) = time.split(":").let { (it.getOrNull(0)?.toIntOrNull() ?: 20) to (it.getOrNull(1)?.toIntOrNull() ?: 0) }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Alarma inexacta: no requiere el permiso de alarmas exactas.
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent(context, time))
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context, "20:00"))
    }
}
