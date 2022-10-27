package com.udacity.ui

import android.Manifest
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.ButtonState
import com.udacity.R
import com.udacity.utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var fileName: String = ""
    private var statusDownload: String = "Fail"
    lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initNotification()
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            custom_button.buttonState = ButtonState.Clicked
            if (downloadListRG.checkedRadioButtonId != (-1)) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        custom_button.buttonState = ButtonState.Loading
                        fileName =
                            (findViewById<RadioButton>(downloadListRG.checkedRadioButtonId)).text.toString()
                        download(downloadListRG.checkedRadioButtonId)
                    } catch (e: Exception) {

                        custom_button.buttonState = ButtonState.Completed

                        Toast.makeText(
                            this,
                            "Please check Internet connection!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

                    custom_button.buttonState = ButtonState.Completed

                }

            } else {
                custom_button.buttonState = ButtonState.Completed

                Toast.makeText(this, "Please choose one to download!", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createNotificationChannel(
                this,
                NotificationUtils.getNewsChannel(this)
            )
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))

            if (id == downloadID) {
                custom_button.buttonState = ButtonState.Completed

                if (cursor.moveToFirst()) {
                    if (cursor.count > 0) {
                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        statusDownload = if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            "Success"
                        } else {
                            "Fail"
                        }
                    }
                }

                NotificationUtils.sendNotification(
                    context = this@MainActivity,
                    titleResId = R.string.notification_title,
                    messageResId = R.string.notification_description,
                    notificationId = NOTIFICATION_ID,
                    statusDownload = statusDownload,
                    fileName = fileName
                )
            }

        }
    }

    private fun download(checkedRadioButtonId: Int) {
        val _URL =
            when (checkedRadioButtonId) {
                R.id.glideRB -> GlideURL
                R.id.udacityRB -> UdacityURL
                R.id.retrofitRB -> RetrofitURL
                R.id.retrofitFailRB -> RetrofitURLFail
                else -> UdacityURL
            }
        val request =
            DownloadManager.Request(Uri.parse(_URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    companion object {
        private const val GlideURL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val UdacityURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RetrofitURL =
            "https://github.com/square/retrofit/archive/master.zip"

        private const val RetrofitURLFail =
            "https://github.com/square/retrofitt/archive/master.zip"

        const val NOTIFICATION_ID = 1000
    }

}
