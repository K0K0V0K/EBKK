package hu.koko.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import hu.koko.MainActivity
import java.util.concurrent.TimeUnit

class MessageService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "ebkk_id",
            "Utazás",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Channel for general notifications"
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        WorkManager.getInstance(applicationContext).enqueue(
            PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES).build())
        LoggerService.log("Message service created.")
    }


}

class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val contextStorage = ContextStorage(applicationContext)
        val lastRun = contextStorage.getLastRun()
        LoggerService.log("Check for rides, Last run: $lastRun")
        if (lastRun == 0L) {
            LoggerService.log("First run for check ride")
            contextStorage.updateLastRun()
            return Result.success()
        }
        if (System.currentTimeMillis() - lastRun < 10 * 60 * 1000) {
            LoggerService.log("Too soon for check run")
            return Result.success()
        }
        FirebaseFirestore.getInstance().collection("travel")
            .whereGreaterThan("created", lastRun)
            .get()
            .addOnSuccessListener { documents -> documents
                .filter { it.getString("driver_name") != contextStorage.getUser() }
                .map { notify(it) }
            }
        contextStorage.updateLastRun()
        return Result.success()
    }

    @SuppressLint("MissingPermission")
    private fun notify(document: QueryDocumentSnapshot) {
        LoggerService.log("Notify about: ${document.getString("origin_city")} -> ${document.getString("destination_city")}")
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, "ebkk_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Uj utazasi lehetoseg!")
            .setContentText("${document.getString("origin_city")} -> ${document.getString("destination_city")}")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
    }

}
