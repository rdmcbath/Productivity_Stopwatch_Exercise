package org.hyperskill.stopwatch

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import java.util.Locale
import kotlin.random.Random
import org.hyperskill.stopwatch.NotificationManager


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isRunning = false
        var seconds = 0
        var upperLimit = 0
        val handler = Handler(Looper.getMainLooper())
        val notificationManager = NotificationManager(this)

        setContentView(R.layout.activity_main)

        /*
            Tests for android can not guarantee the correctness of solutions that make use of
            mutation on "static" variables to keep state. You should avoid using those.
            Consider "static" as being anything on kotlin that is transpiled to java
            into a static variable. That includes global variables and variables inside
            singletons declared with keyword object, including companion object.
            This limitation is related to the use of JUnit on tests. JUnit re-instantiate all
            instance variable for each test method, but it does not re-instantiate static variables.
            The use of static variable to hold state can lead to state from one test to spill over
            to another test and cause unexpected results.
            Using mutation on static variables to keep state
            is considered a bad practice anyway and no measure
            attempting to give support to that pattern will be made.
         */

        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        val timeView = findViewById<TextView>(R.id.textView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.isVisible = false

        val updateTimer = object : Runnable {
            override fun run() {
                val minutes = seconds / 60
                val secs = seconds % 60
                timeView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
                val randomColor =
                    Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
                progressBar.indeterminateTintList = ColorStateList.valueOf(randomColor)
                progressBar.isVisible = true
                val textColor = if (upperLimit in 1 until seconds) Color.RED else Color.DKGRAY
                timeView.setTextColor(textColor)
                if (isRunning) {
                    settingsButton.isEnabled = false
                    if (upperLimit != 0 && seconds < upperLimit) {
                        seconds++
                    } else {
                        seconds++
                    }
                    if (upperLimit in 1 until seconds) {
                        notificationManager.showNotification("Notification", "Time Exceeded")
                    }
                    handler.postDelayed(this, 1000)
                }
            }
        }

        startButton.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                handler.post(updateTimer)
            }
        }

        resetButton.setOnClickListener {
            isRunning = false
            progressBar.isVisible = false
            seconds = 0
            timeView.text = getString(R.string.text_view_start_state)
            timeView.setTextColor(Color.DKGRAY)
            settingsButton.isEnabled = true
            handler.removeCallbacks(updateTimer)
        }

        settingsButton.setOnClickListener {
            val contentView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null, false)
            AlertDialog.Builder(this)
                .setTitle("Set upper limit in seconds")
                .setView(contentView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val editText = contentView.findViewById<EditText>(R.id.upperLimitEditText)
                    Toast.makeText(this, "You selected ${editText.text}", Toast.LENGTH_SHORT).show()
                    upperLimit = try {
                        editText.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                    handler.post(updateTimer)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                .show()
        }
    }
}