package dev.mullion.view

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Edge-to-edge for View screens, with one insets calculation for status bars,
 * gesture bars and the IME.
 *
 * On API 21 the compat layer reports the IME as zero until the keyboard is
 * attached; once it is, content lifts by exactly the IME height. The listener
 * preserves the view's original padding and adds the insets, so it can be
 * installed on any existing root.
 */
public object MullionEdgeToEdge {

    /**
     * Draws behind the system bars. Call before `setContentView`.
     */
    @JvmStatic
    public fun enable(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.navigationBarColor = Color.TRANSPARENT
        }
        WindowInsetsControllerCompat(activity.window, activity.window.decorView)
            .isAppearanceLightStatusBars = false
    }

    /**
     * Installs an insets listener that pads the root by system bars and (when
     * [includeIme]) the keyboard.
     *
     * @param consume when `true`, descendants will not receive the same insets.
     * @param onInsets optional observer for drawing inset bands.
     */
    @JvmStatic
    @JvmOverloads
    public fun install(
        root: View,
        includeIme: Boolean = true,
        applyPadding: Boolean = true,
        consume: Boolean = false,
        onInsets: ((WindowInsetsCompat) -> Unit)? = null,
    ) {
        val initialLeft = root.paddingLeft
        val initialTop = root.paddingTop
        val initialRight = root.paddingRight
        val initialBottom = root.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = if (includeIme) {
                insets.getInsets(WindowInsetsCompat.Type.ime())
            } else {
                androidx.core.graphics.Insets.NONE
            }
            if (applyPadding) {
                view.setPadding(
                    initialLeft + bars.left,
                    initialTop + bars.top,
                    initialRight + bars.right,
                    initialBottom + maxOf(bars.bottom, ime.bottom),
                )
            }
            onInsets?.invoke(insets)
            if (consume) WindowInsetsCompat.CONSUMED else insets
        }
        ViewCompat.requestApplyInsets(root)
    }

    /** Removes the listener. Padding is left as-is. */
    @JvmStatic
    public fun clear(root: View) {
        ViewCompat.setOnApplyWindowInsetsListener(root, null)
        ViewCompat.requestApplyInsets(root)
    }
}
