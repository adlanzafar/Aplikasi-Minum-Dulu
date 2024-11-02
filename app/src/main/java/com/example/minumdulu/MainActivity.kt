package com.example.minumdulu

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textViewConsumed: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressBarHorizontal: ProgressBar
    private lateinit var button50ml: ImageButton
    private lateinit var button100ml: ImageButton
    private lateinit var button250ml: ImageButton
    private lateinit var button350ml: ImageButton
    private lateinit var button500ml: ImageButton
    private lateinit var buttonSchedule: ImageButton
    private lateinit var buttonActivities: ImageButton
    private lateinit var buttonSettings: ImageButton

    private var currentProgress: Int = 0
    private val targetProgress: Int = 10000
    private var entryCount: Int = 0

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "minum_dulu_channel"
        const val SHARED_PREFS_NAME = "minumDuluPrefs"
        const val KEY_ACTIVITY_LIST = "activityList"
        const val KEY_CURRENT_PROGRESS = "currentProgress"
        const val KEY_CURRENT_DATE = "currentDate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        loadProgress()
        setButtonClickListeners()
        createNotificationChannel()
        requestPermissions()
    }

    private fun initializeViews() {
        textViewConsumed = findViewById(R.id.textViewConsumed)
        progressBar = findViewById(R.id.progressBar)
        progressBarHorizontal = findViewById(R.id.progressBarHorizontal)
        button50ml = findViewById(R.id.imageButton50ml)
        button100ml = findViewById(R.id.imageButton100ml)
        button250ml = findViewById(R.id.imageButton250ml)
        button350ml = findViewById(R.id.imageButton350ml)
        button500ml = findViewById(R.id.imageButton500ml)
        buttonSchedule = findViewById(R.id.buttonSchedule)
        buttonActivities = findViewById(R.id.buttonActivities)
        buttonSettings = findViewById(R.id.buttonSettings)
    }

    private fun setButtonClickListeners() {
        button50ml.setOnClickListener { addToProgress(50) }
        button100ml.setOnClickListener { addToProgress(100) }
        button250ml.setOnClickListener { addToProgress(250) }
        button350ml.setOnClickListener { addToProgress(350) }
        button500ml.setOnClickListener { addToProgress(500) }

        buttonSchedule.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }

        buttonActivities.setOnClickListener {
            startActivity(Intent(this, ActivityListActivity::class.java))
        }

        buttonSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun addToProgress(amount: Int) {
        currentProgress += amount
        if (currentProgress > targetProgress) {
            currentProgress = targetProgress
        }
        progressBar.progress = currentProgress
        progressBarHorizontal.progress = currentProgress
        updateConsumedText()
        saveProgress(amount)
    }

    private fun saveProgress(amount: Int) {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val activities = sharedPreferences.getStringSet(KEY_ACTIVITY_LIST, mutableSetOf())?.toMutableSet()

        val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        if (amount > 0) {
            entryCount++
            val entry = "$entryCount: : $currentDate : $currentTime : $amount ml"
            activities?.add(entry)
        }

        editor.putStringSet(KEY_ACTIVITY_LIST, activities)
        editor.putInt(KEY_CURRENT_PROGRESS, currentProgress)
        editor.putString(KEY_CURRENT_DATE, currentDate)
        editor.apply()
    }

    private fun updateConsumedText() {
        textViewConsumed.text = getString(R.string.konsumsi, currentProgress)
    }

    private fun loadProgress() {
        val sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        currentProgress = sharedPreferences.getInt(KEY_CURRENT_PROGRESS, 0)
        progressBar.progress = currentProgress
        progressBarHorizontal.progress = currentProgress
        updateConsumedText()
    }

    override fun onPause() {
        super.onPause()
        saveProgress(0)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channelDeskripsi)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SET_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SET_ALARM),
                1
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    2
                )
            }
        }
    }
}
