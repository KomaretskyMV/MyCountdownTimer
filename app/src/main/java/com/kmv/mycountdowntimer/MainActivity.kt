package com.kmv.mycountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.kmv.mycountdowntimer.databinding.ActivityMainBinding
import kotlinx.coroutines.*

private const val INSTANCE_STATE = "instance state"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var currentProgress: Int = 0
    private var maxProgress: Int = 10
    private var isTimerRun = false
        get() = timerStart.isActive
    private val timerScope = CoroutineScope(Dispatchers.Main)
    private var timerStart = timerScope.launch { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savedInstanceState?.let {
            currentProgress = it.getInt(INSTANCE_STATE, 10)
            maxProgress = it.getInt(INSTANCE_STATE, 10)
            isTimerRun = it.getBoolean(INSTANCE_STATE)
        }

        if (!isTimerRun) {
            timerStart.start()
        }

        setProgress()

        binding.startButton.setOnClickListener {
            startTimer()
        }

        binding.stopButton.setOnClickListener {
            stopTimer()
        }

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            maxProgress = binding.slider.value.toInt()
            binding.progressCircular.max = maxProgress
            setProgress()
        }

        binding.themeButton.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }
        }

    }

    override fun onRestart() {
        super.onRestart()
        if (!isTimerRun) {
            timerStart.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerStart.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INSTANCE_STATE, currentProgress)
        outState.putInt(INSTANCE_STATE, maxProgress)
        outState.putBoolean(INSTANCE_STATE, isTimerRun)
    }

    /*override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentProgress = savedInstanceState.getInt(INSTANCE_STATE)
        maxProgress = savedInstanceState.getInt(INSTANCE_STATE)
        isTimerRun = savedInstanceState.getBoolean(INSTANCE_STATE)
    }*/

    private fun startTimer() {
        binding.startButton.visibility = View.INVISIBLE
        binding.stopButton.visibility = View.VISIBLE
        binding.slider.isEnabled = false
//        currentProgress = binding.slider.value.toInt()

        timerStart = timerScope.launch {
            for (i in maxProgress - 1 downTo 0) {
                delay(1000)
                binding.progressCircular.progress--
                binding.timerText.text = i.toString()
                currentProgress = i
            }
            stopTimer()
        }
    }

    private fun stopTimer() {
        binding.stopButton.visibility = View.INVISIBLE
        binding.startButton.visibility = View.VISIBLE
        binding.slider.isEnabled = true
        binding.progressCircular.progress = maxProgress
        binding.timerText.text = maxProgress.toString()
        timerStart.cancel()
        Toast.makeText(this, "Timer Task Finished", Toast.LENGTH_SHORT).show()
    }

    private fun setProgress() {
        binding.progressCircular.progress = maxProgress
        binding.timerText.text = maxProgress.toString()
    }
}