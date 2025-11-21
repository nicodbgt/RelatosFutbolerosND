package com.app.relatosfutbolerosnd.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class MatchOverlayService : Service() {

    companion object {
        var isServiceRunning = false
        var listener: OverlayServiceListener? = null

        const val ACTION_START_OVERLAY = "START_OVERLAY"
        const val ACTION_STOP_OVERLAY = "STOP_OVERLAY"
        const val ACTION_UPDATE_SCORE = "UPDATE_SCORE"
        const val ACTION_UPDATE_TIME = "UPDATE_TIME"
        const val EXTRA_TEAM1 = "EXTRA_TEAM1"
        const val EXTRA_TEAM2 = "EXTRA_TEAM2"
        const val EXTRA_TEAM1_SCORE = "EXTRA_TEAM1_SCORE"
        const val EXTRA_TEAM2_SCORE = "EXTRA_TEAM2_SCORE"
        const val EXTRA_MATCH_TIME = "EXTRA_MATCH_TIME"
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private var dropdownMenuView: View? = null

    private lateinit var timerTextView: TextView
    private lateinit var scoreTextView: TextView
    private var playPauseMenuItem: TextView? = null

    private var timer: Timer? = null
    private var seconds = 0
    var isPaused = true  // Inicia pausado por defecto
        private set

    private var team1Name = "Equipo 1"
    private var team2Name = "Equipo 2"
    var team1Score = 0
        private set
    var team2Score = 0
        private set
    private var isMenuOpen = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_OVERLAY -> {
                team1Name = intent.getStringExtra(EXTRA_TEAM1) ?: team1Name
                team2Name = intent.getStringExtra(EXTRA_TEAM2) ?: team2Name
                startOverlay()
            }
            ACTION_STOP_OVERLAY -> stopOverlay()
            ACTION_UPDATE_SCORE -> {
                team1Score = intent.getIntExtra(EXTRA_TEAM1_SCORE, team1Score)
                team2Score = intent.getIntExtra(EXTRA_TEAM2_SCORE, team2Score)
                updateScoreText()
            }
            ACTION_UPDATE_TIME -> {
                val time = intent.getStringExtra(EXTRA_MATCH_TIME)
                time?.let { updateTimerText(it) }
            }
        }

        return START_STICKY
    }

    @Suppress("DEPRECATION")
    private fun startOverlay() {
        if (::overlayView.isInitialized && overlayView.windowToken != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        overlayView = createOverlayView()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.FILL_HORIZONTAL
        }

        windowManager.addView(overlayView, params)
        isServiceRunning = true
        listener?.onOverlayStarted()
    }

    @SuppressLint("SetTextI18n")
    private fun createOverlayView(): View {
        val overlay = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#CC000000"))  // Más transparente para streaming
            setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
            gravity = Gravity.CENTER_VERTICAL
        }

        scoreTextView = TextView(this).apply {
            textSize = 18f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.START
        }

        timerTextView = TextView(this).apply {
            text = "00:00"
            textSize = 18f
            setTextColor(Color.WHITE)
            setPadding(0, 0, dpToPx(20), 0)
        }

        val menuIcon = TextView(this).apply {
            text = "⋮"
            textSize = 22f
            setTextColor(Color.WHITE)
            setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
            setOnClickListener { toggleDropdownMenu() }
        }

        overlay.addView(scoreTextView)
        overlay.addView(timerTextView)
        overlay.addView(menuIcon)
        updateScoreText()
        return overlay
    }

    private fun toggleDropdownMenu() {
        if (isMenuOpen) {
            hideDropdownMenu()
        } else {
            showDropdownMenu()
        }
    }

    @SuppressLint("InflateParams")
    private fun showDropdownMenu() {
        if (dropdownMenuView != null) return

        dropdownMenuView = createDropdownMenuView()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            y = dpToPx(50)
            x = dpToPx(10)
        }
        windowManager.addView(dropdownMenuView, params)
        isMenuOpen = true
    }

    private fun hideDropdownMenu() {
        dropdownMenuView?.let {
            if (it.windowToken != null) {
                windowManager.removeView(it)
            }
        }
        dropdownMenuView = null
        isMenuOpen = false
    }

    private fun createDropdownMenuView(): View {
        val menu = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC000000"))
            setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))
        }

        playPauseMenuItem = createDropdownItem(if (isPaused) "▶ Iniciar" else "⏸️ Pausar") {
            togglePlayPause()
        }

        menu.addView(createDropdownItem("+1 ${team1Name.take(10)}") {
            team1Score++; updateScoreText(); notifyScoreUpdate()
        })
        menu.addView(createDropdownItem("-1 ${team1Name.take(10)}") {
            if (team1Score > 0) team1Score--; updateScoreText(); notifyScoreUpdate()
        })
        menu.addView(createDropdownItem("+1 ${team2Name.take(10)}") {
            team2Score++; updateScoreText(); notifyScoreUpdate()
        })
        menu.addView(createDropdownItem("-1 ${team2Name.take(10)}") {
            if (team2Score > 0) team2Score--; updateScoreText(); notifyScoreUpdate()
        })
        menu.addView(playPauseMenuItem)
        menu.addView(createDropdownItem("🔄 Reiniciar") { resetMatch() })

        return menu
    }

    private fun createDropdownItem(text: String, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            this.text = text
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10))
            setOnClickListener {
                onClick()
                hideDropdownMenu()
            }
        }
    }

    private fun togglePlayPause() {
        if (isPaused) {
            startTimer()
        } else {
            pauseTimer()
        }
    }

    private fun startTimer() {
        if (timer != null) return
        isPaused = false
        timer = Timer()
        val handler = Handler(Looper.getMainLooper())

        timer?.scheduleAtFixedRate(timerTask {
            if (!isPaused) {
                seconds++
                val minutes = seconds / 60
                val remaining = seconds % 60
                handler.post {
                    timerTextView.text = String.format("%02d:%02d", minutes, remaining)
                    listener?.onTimeUpdated(String.format("%02d:%02d", minutes, remaining))
                }
            }
        }, 0, 1000)

        playPauseMenuItem?.text = "⏸️ Pausar"
        listener?.onMatchStarted()
    }

    private fun pauseTimer() {
        isPaused = true
        playPauseMenuItem?.text = "▶ Reanudar"
        listener?.onMatchPaused()
    }

    private fun resetMatch() {
        seconds = 0
        team1Score = 0
        team2Score = 0
        isPaused = true

        timer?.cancel()
        timer = null

        timerTextView.text = "00:00"
        updateScoreText()
        playPauseMenuItem?.text = "▶ Iniciar"
        listener?.onMatchReset()
    }

    @SuppressLint("SetTextI18n")
    private fun updateScoreText() {
        if (::scoreTextView.isInitialized) {
            scoreTextView.text = "$team1Name $team1Score - $team2Score $team2Name"
        }
    }

    private fun updateTimerText(time: String) {
        if (::timerTextView.isInitialized) {
            timerTextView.text = time
        }
    }

    private fun notifyScoreUpdate() {
        listener?.onScoreUpdated(team1Score, team2Score)
    }

    private fun stopOverlay() {
        if (::overlayView.isInitialized && overlayView.windowToken != null) {
            windowManager.removeView(overlayView)
        }
        hideDropdownMenu()

        timer?.cancel()
        timer = null
        seconds = 0
        team1Score = 0
        team2Score = 0
        isPaused = true

        isServiceRunning = false
        listener?.onOverlayStopped()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    interface OverlayServiceListener {
        fun onOverlayStarted()
        fun onOverlayStopped()
        fun onMatchStarted()
        fun onMatchPaused()
        fun onMatchReset()
        fun onScoreUpdated(team1Score: Int, team2Score: Int)
        fun onTimeUpdated(time: String)
    }
}