package com.ander.contadormus

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import org.json.JSONArray

/**
 * Contador Mus: la app es una WebView a pantalla completa que carga assets/index.html.
 * Esta actividad añade lo que la web sola no puede hacer bien:
 *  - bloquear la pantalla en horizontal (y soltarla solo si se activa en ajustes)
 *  - mantener la pantalla encendida
 *  - modo inmersivo (sin barras de sistema)
 *  - vibración
 *  - botón atrás: cierra ventanas y pide doble pulsación para salir
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private var lastBack = 0L

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Horizontal por defecto (cualquiera de los dos lados); la web lo puede liberar
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        web = WebView(this).apply {
            setBackgroundColor(Color.parseColor("#0d1712"))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true          // localStorage: guarda la partida
            settings.allowFileAccess = true
            settings.mediaPlaybackRequiresUserGesture = true
            isHapticFeedbackEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            webViewClient = WebViewClient()
            addJavascriptInterface(Bridge(), "Android")
        }
        setContentView(web)

        if (savedInstanceState != null) web.restoreState(savedInstanceState)
        else web.loadUrl("file:///android_asset/index.html")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    @Deprecated("Usado a propósito: sin dependencias de AndroidX")
    override fun onBackPressed() {
        // Primero se le pregunta a la web si tiene una ventana abierta que cerrar
        web.evaluateJavascript("window.handleBack ? window.handleBack() : false") { handled ->
            if (handled == "true") return@evaluateJavascript
            val now = System.currentTimeMillis()
            if (now - lastBack < 2000) {
                finish()
            } else {
                lastBack = now
                Toast.makeText(this, "Pulsa atrás otra vez para salir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        web.destroy()
        super.onDestroy()
    }

    /** Funciones que la web puede llamar como window.Android.xxx() */
    inner class Bridge {

        /** pattern: JSON con milisegundos, p. ej. "[25]" o "[40,60,40]" (vibra, pausa, vibra…) */
        @JavascriptInterface
        fun vibrate(pattern: String) {
            try {
                val arr = JSONArray(pattern)
                val times = LongArray(arr.length() + 1)
                times[0] = 0 // sin espera inicial
                for (i in 0 until arr.length()) times[i + 1] = arr.getLong(i)
                val effect = if (arr.length() == 1)
                    VibrationEffect.createOneShot(times[1], VibrationEffect.DEFAULT_AMPLITUDE)
                else
                    VibrationEffect.createWaveform(times, -1)
                vibrator()?.vibrate(effect)
            } catch (e: Exception) { }
        }

        /** true: deja girar a vertical; false: solo horizontal */
        @JavascriptInterface
        fun setPortraitAllowed(allowed: Boolean) {
            runOnUiThread {
                val wanted = if (allowed) ActivityInfo.SCREEN_ORIENTATION_FULL_USER
                             else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                if (requestedOrientation != wanted) requestedOrientation = wanted
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrator(): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        else
            getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
}
