package com.plasdor.app.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.plasdor.app.R
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants
import com.plasdor.app.view.activity.AdminHomeActivity

class MyFirebaseInstanceIDService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Notification Message: $remoteMessage")

        var title =remoteMessage.notification?.title
        var description =remoteMessage.data["DESCRIPTION"]
        var imageURL = remoteMessage.notification?.imageUrl



        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var channelId = getString(R.string.channel_id)

        val intent = Intent(this, AdminHomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("ImFrom","Notification")
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)


        var notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.new_logo)
            .setContentTitle(remoteMessage.data["TITLE"])
            .setContentText(remoteMessage.data["DESCRIPTION"])
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(2)//high
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setLights(Color.GREEN, 1000, 1000)
            .setContentIntent(pendingIntent)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                val channel = NotificationChannel(channelId, getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()

        SharePreferenceManager.getInstance(applicationContext).save(Constants.TOKEN, token)
        SharePreferenceManager.getInstance(applicationContext).save(Constants.IS_TOKEN_UPDATE, true)
        SharePreferenceManager.getInstance(applicationContext)
            .save(Constants.IS_TOKEN_SAVE_API_CALLED, false)
        SharePreferenceManager.getInstance(applicationContext).save(Constants.DEVICE_ID, deviceId)
    }


    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(from: String?, body: String?) {

    }

    companion object {
        private const val TAG = "MyFirebaseInstance"
    }
}