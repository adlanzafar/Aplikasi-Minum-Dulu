package com.example.minumdulu

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ScheduleActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var buttonAddAlarm: Button
    private lateinit var buttonClearAlarms: Button
    private lateinit var alarmListLayout: LinearLayout
    private lateinit var preferences: SharedPreferences
    private val alarmIntents = mutableListOf<PendingIntent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        timePicker = findViewById(R.id.timePicker)
        buttonAddAlarm = findViewById(R.id.buttonAddAlarm)
        buttonClearAlarms = findViewById(R.id.buttonClearAlarms)
        alarmListLayout = findViewById(R.id.alarmListLayout)

        preferences = getSharedPreferences("alarms", Context.MODE_PRIVATE)

        buttonAddAlarm.setOnClickListener { scheduleAlarm() }
        buttonClearAlarms.setOnClickListener { clearAlarms() }

        loadAlarmsFromPreferences()
    }

    private fun scheduleAlarm() {
        val hour = timePicker.hour
        val minute = timePicker.minute

        val intent = Intent(this, AlarmReceiver::class.java).apply {
            action = "com.example.minumdulu.ACTION_ALARM_RECEIVER"
            putExtra("notificationId", hour * 60 + minute)
            putExtra("message", getString(R.string.teksNotifikasi))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this, hour * 60 + minute, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setAlarmClock(
                    AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent), pendingIntent
                )
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        alarmIntents.add(pendingIntent)

        saveAlarmToPreferences(hour, minute)
        addAlarmView(hour, minute)

        Toast.makeText(this, "Alarm set for $hour:$minute", Toast.LENGTH_SHORT).show()
    }

    private fun addAlarmView(hour: Int, minute: Int) {
        val alarmButton = Button(this).apply {
            text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            setTextColor(Color.BLACK)
            setOnClickListener { removeAlarm(this) }
        }
        alarmListLayout.addView(alarmButton)
    }

    private fun removeAlarm(button: Button) {
        val (hour, minute) = button.text.split(":").map { it.toInt() }
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, hour * 60 + minute, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        alarmListLayout.removeView(button)
        removeAlarmFromPreferences(hour, minute)

        Toast.makeText(this, "Alarm for $hour:$minute removed", Toast.LENGTH_SHORT).show()
    }

    private fun clearAlarms() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntents.forEach { alarmManager.cancel(it) }
        alarmIntents.clear()
        alarmListLayout.removeAllViews()
        clearPreferences()

        Toast.makeText(this, "All alarms cleared", Toast.LENGTH_SHORT).show()
    }

    private fun saveAlarmToPreferences(hour: Int, minute: Int) {
        preferences.edit().putBoolean("$hour:$minute", true).apply()
    }

    private fun removeAlarmFromPreferences(hour: Int, minute: Int) {
        preferences.edit().remove("$hour:$minute").apply()
    }

    private fun clearPreferences() {
        preferences.edit().clear().apply()
    }

    private fun loadAlarmsFromPreferences() {
        preferences.all.keys.forEach {
            val (hour, minute) = it.split(":").map { time -> time.toInt() }
            addAlarmView(hour, minute)
        }
    }
}
