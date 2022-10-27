package com.udacity.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.R
import com.udacity.ui.DetailActivity

object NotificationUtils {
    private const val CHANNEL_ID = "downloadFile"
    private const val CHANNEL_NAME = "DownloadFile"
    private const val CHANNEL_DES = "Download File Completed"
    private const val REQUEST_CODE = 1000

    @SuppressLint("InlinedApi")
    fun getNewsChannel(context: Context): ChannelDetails {
        return ChannelDetails(
            CHANNEL_ID,
            CHANNEL_NAME,
            CHANNEL_DES,
            NotificationManager.IMPORTANCE_HIGH,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.VISIBILITY_PUBLIC
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context, channelDetails: ChannelDetails) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        NotificationChannel(
            channelDetails.id,
            channelDetails.name,
            channelDetails.importance
        ).apply {
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
            lightColor = context.getColor(R.color.colorAccent)
            description = channelDetails.description
            lockscreenVisibility = channelDetails.visibility
            notificationManager.createNotificationChannel(this)
        }
    }

    fun sendNotification(
        context: Context,
        @StringRes titleResId: Int,
        @StringRes messageResId: Int,
        notificationId: Int,
        statusDownload: String,
        fileName: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val title = context.getString(titleResId)
        val message = context.getString(messageResId)

        val notifyIntent = Intent(context, DetailActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        notifyIntent.putExtras(
            Bundle().apply {
                putString("fileName", fileName)
                putString("status", statusDownload)
            }
        )

        val pendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val newsChannel = getNewsChannel(context)

        val notification = NotificationCompat.Builder(context, newsChannel.id)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .setLights(ContextCompat.getColor(context, R.color.grey_200), 1000, 3000)
            .setVisibility(newsChannel.visibility)
            .setPriority(newsChannel.priority)
            .addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.notification_button),
                    pendingIntent
                )
            )
            .build()

        notificationManager.notify(notificationId, notification)
    }


    fun clearNotification(context: Context, notificationId: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.cancel(notificationId)
    }
}