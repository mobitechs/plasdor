package com.plasdor.app.firebase

import android.content.Context
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.plasdor.app.session.SharePreferenceManager
import com.plasdor.app.utils.Constants

class MyFirebaseInstanceIDService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Notification Message: $remoteMessage")

        var title =remoteMessage.notification?.title
        var description =remoteMessage.notification?.body
        var imageURL =remoteMessage.notification?.imageUrl

//        val intent = Intent(this, NotificationActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        intent.putExtra("title",title)
//        intent.putExtra("description",description)
//        intent.putExtra("imageURL",imageURL)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.in_notification)
//            .setContentTitle("ptk "+title)
//            .setContentText(description)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
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