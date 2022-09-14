package com.codecoy.bahdjol.notification


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codecoy.bahdjol.constant.Constant.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService: FirebaseMessagingService() {

    private lateinit var sharedPreferences: SharedPreferences



    override fun onNewToken(deviceToken: String) {
        super.onNewToken(deviceToken)

        Log.i(TAG, "onNewToken: $deviceToken")

        saveTokenIntoPref(deviceToken)
    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.i(TAG, ": onMessageReceived ${p0.data}")

        showNotification(p0.data)

    }

    private fun showNotification(data: MutableMap<String, String>) {

        // Create an explicit intent for an Activity in your app
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//
//
//        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.logo)
//            .setContentTitle(data["title"])
//            .setContentText(data["body"])
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//
//
//
//
//        with(NotificationManagerCompat.from(this)) {
//            // notificationId is a unique int for each notification that you must define
//            notify(1, builder.build())
//        }

    }

    private fun saveTokenIntoPref(deviceToken: String) {

       sharedPreferences = getSharedPreferences("DeviceToken", Context.MODE_PRIVATE)

        val editor: SharedPreferences.Editor = sharedPreferences.edit()

        editor.putString("deviceToken", deviceToken)

        Log.i(TAG, "deviceToken: $deviceToken")

        editor.apply()

    }

}