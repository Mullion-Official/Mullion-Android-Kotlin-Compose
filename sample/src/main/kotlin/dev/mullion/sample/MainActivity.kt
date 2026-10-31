package dev.mullion.sample

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.mullion.compose.theme.MullionTheme
import dev.mullion.sample.reference.ReferenceApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            MullionTheme {
                ReferenceApp()
            }
        }

        // A kiosk/demo flag for clean screen recordings. Normal launches keep
        // the system bars; the reference app itself is still edge-to-edge.
        if (intent?.getBooleanExtra(EXTRA_DEMO_FULLSCREEN, false) == true) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.decorView.post {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    companion object {
        const val EXTRA_DEMO_FULLSCREEN = "demo_fullscreen"
    }
}
