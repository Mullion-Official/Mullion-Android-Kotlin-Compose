package dev.mullion.sample.showcase

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.currentBreakpoint
import dev.mullion.core.MullionMetrics
import dev.mullion.sample.ui.Lab
import dev.mullion.sample.ui.SampleBody
import dev.mullion.sample.ui.SectionHeader
import dev.mullion.view.MullionButtonView
import dev.mullion.view.MullionCardView
import kotlin.math.roundToInt

/**
 * Proof that the View tier shares the same ruler and the same glass.
 *
 * This is a real AndroidView with a real `MullionCardView` and a real
 * `MullionButtonView`; it is not a Compose imitation.
 */
@Composable
fun ViewInteropSection() {
    val bp = currentBreakpoint()
    val metrics = MullionMetrics.forBp(bp)
    val compact = bp.isCompact

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glass(shape = RoundedCornerShape(28.dp), level = GlassLevel.Window)
            .padding(22.dp),
    ) {
        SectionHeader(
            kicker = "Interop / View tier",
            title = "View 也能吃到同一把尺。",
            description = "MullionBreakpointLayout、MullionPaneLayout、MullionCardView、MullionButtonView 与 ResourceScaler，直接放进既有 XML 工程；断点规则与 Compose 侧共用 mullion-core。",
        )

        if (compact) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                InteropViewCard(Modifier.fillMaxWidth().height(190.dp))
                InteropMetrics(metrics, Modifier.fillMaxWidth())
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                InteropViewCard(Modifier.width(320.dp).height(190.dp))
                InteropMetrics(metrics, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun InteropViewCard(modifier: Modifier) {
    AndroidView(
        factory = { context ->
            MullionCardView(context).apply {
                setPadding(dp(context, 20), dp(context, 20), dp(context, 20), dp(context, 20))
                val column = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
                val title = TextView(context).apply {
                    text = "MULLION / VIEW"
                    typeface = Typeface.MONOSPACE
                    setTextColor(Color.parseColor("#EDF4FA"))
                    textSize = 12f
                    letterSpacing = 0.1f
                }
                val body = TextView(context).apply {
                    text = "MullionCardView(ctx)\nMullionButtonView(ctx)\nResourceScaler.scaled(R.dimen.mullion_spacing_section)"
                    typeface = Typeface.MONOSPACE
                    setTextColor(Color.parseColor("#94A9BB"))
                    textSize = 10f
                    setLineSpacing(0f, 1.5f)
                    setPadding(0, dp(context, 10), 0, dp(context, 14))
                }
                val action = MullionButtonView(context).apply {
                    text = "VIEW BUTTON"
                    setOnClickListener {
                        title.text = if (title.text.toString().endsWith("✓")) {
                            "MULLION / VIEW"
                        } else {
                            "MULLION / VIEW ✓"
                        }
                    }
                }
                column.addView(title)
                column.addView(body)
                column.addView(
                    action,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ),
                )
                addView(column)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun InteropMetrics(metrics: MullionMetrics, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Lab("Shared metrics", color = MullionColors.Volt)
        InteropRow("section", "${metrics.sectionSpacingDp}dp")
        InteropRow("display", "${metrics.displayTypeSp}sp")
        InteropRow("gutter", "${metrics.paneGutterDp}dp")
        InteropRow("minSdk", "21")
        SampleBody(
            "AndroidView 里的点击、字体与 1dp 边框全部交给平台 View 处理。",
            size = 11,
        )
    }
}

@Composable
private fun InteropRow(key: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = key,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = MullionColors.Faint,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.5.sp),
            color = MullionColors.Volt,
        )
    }
}

private fun dp(context: Context, value: Int): Int =
    (value * context.resources.displayMetrics.density).roundToInt()
