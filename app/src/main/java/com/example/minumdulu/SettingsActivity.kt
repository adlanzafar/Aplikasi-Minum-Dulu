package com.example.minumdulu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        buttonReset = findViewById(R.id.buttonReset)
        buttonReset.setOnClickListener {
            resetApp()
        }
    }

    private fun resetApp() {
        val sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            clear()
            apply()
        }
        restartMainActivity()
    }

    private fun restartMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}
