package hu.koko

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hu.koko.service.MessageService

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, MessageService::class.java)
            context.startService(serviceIntent)
        }
    }
}
