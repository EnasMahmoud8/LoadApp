package com.udacity.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.udacity.R
import com.udacity.ui.MainActivity.Companion.NOTIFICATION_ID
import com.udacity.utils.NotificationUtils
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras
        extras?.let {
            titleTV.text = it.getString("fileName")
            statusTV.text = it.getString("status")

            if (it.getString("status")!! == "Fail")
                statusTV.setTextColor(ColorStateList.valueOf(resources.getColor(R.color.red_700)))
        }

        NotificationUtils.clearNotification(this, NOTIFICATION_ID)

        okBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }


}
