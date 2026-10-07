package com.hormi.hormiapp.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Reprograma el recordatorio cuando el dispositivo se reinicia. */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = UserPreferencesRepository(context.applicationContext)
                if (prefs.reminderEnabled.first()) {
                    ReminderScheduler.schedule(context, prefs.reminderTime.first())
                }
            } finally {
                pending.finish()
            }
        }
    }
}
