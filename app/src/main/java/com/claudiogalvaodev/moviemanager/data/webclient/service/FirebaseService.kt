package com.claudiogalvaodev.moviemanager.data.webclient.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.claudiogalvaodev.moviemanager.R
import com.claudiogalvaodev.moviemanager.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    @SuppressLint("UnspecifiedImmutableFlag", "ObsoleteSdkInt")
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val intent = Intent(this, MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNoficationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
            .setSmallIcon(R.drawable.logo_negativa)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    private fun createNoficationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.BLUE
        }
        notificationManager.createNotificationChannel(channel)
    }
}