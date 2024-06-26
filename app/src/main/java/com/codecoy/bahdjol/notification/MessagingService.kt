package com.codecoy.bahdjol.notification


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.codecoy.bahdjol.MainActivity
import com.codecoy.bahdjol.R
import com.codecoy.bahdjol.constant.Constant.TAG
import com.codecoy.bahdjol.utils.ServiceIds.CHANNEL_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MessagingService() : FirebaseMessagingService() {

    private val bookingGroup = "com.android.example.BOOKING_GROUP"
    private var summaryId: Long = 1

    private lateinit var sharedPreferences: SharedPreferences

    private var notString: String? = null

    private lateinit var intent: Intent


    override fun onNewToken(deviceToken: String) {
        super.onNewToken(deviceToken)

        Log.i(TAG, "onNewToken: $deviceToken")

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        Log.i(TAG, ": onMessageReceived ${p0.data}")

        showNotification(p0.data)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showNotification(data: MutableMap<String, String>) {

//         Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).putExtra("myKey", 5).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ukey",5)
        }

        notString = data["body"].toString().trim().lowercase(Locale.ROOT)
        notString = notString!!.filterNot {
            it.isWhitespace()
        }


        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        Log.i(TAG, "showNotification: $notString")

        if (notString!!.contains("agent")) {

            Log.i(TAG, "showNotification: true")

            sharedPreferences = this.getSharedPreferences("notifiInfo", Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            editor.putString("notifi", "1")

            editor.apply()

        } else {

            Log.i(TAG, "showNotification: false")


            sharedPreferences = this.getSharedPreferences("notifiInfo", Context.MODE_PRIVATE)

            val editor: SharedPreferences.Editor = sharedPreferences.edit()

            editor.putString("notifi", "2")

            editor.apply()


        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }



        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.bahdjol_logo_)
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setGroup(bookingGroup)
            .setGroupSummary(true)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify((++summaryId).toInt(), builder.build())
        }

    }



}