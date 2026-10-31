package dev.mullion.sample.showcase

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.adaptive.AdaptiveDialog
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.sample.ui.Lab

/**
 * A compact jump table for the 26 live demos.
 *
 * Indices match the sample's LazyColumn: adaptive cards start at 6, the base
 * workbench is 20. The list is intentionally host-side; the framework itself
 * does not own page routing.
 */
@Composable
fun IndexOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(visible) {
        if (visible) scrollState.scrollTo(0)
    }

    AdaptiveDialog(
        visible = visible,
        onDismissRequest = onDismiss,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Lab("Index / 26 modules", color = MullionColors.Volt)
            Spacer(Modifier.weight(1f))
            Text(
                text = "点击跳转",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
            )
        }
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 460.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            adaptiveSpecs.forEach { spec ->
                IndexRow(
                    number = spec.number,
                    name = spec.name,
                    onClick = { onSelect(5 + spec.number) },
                )
            }
            Spacer(Modifier.height(8.dp))
            IndexRow(number = 15, name = "Base Kit / Button", onClick = { onSelect(20) })
            Text(
                text = "基座 12 件共享同一标尺，点击进入工作台。",
                style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
                color = MullionColors.Faint,
                modifier = Modifier.padding(start = 34.dp, top = 2.dp, bottom = 6.dp),
            )
            baseSpecs.forEach { spec ->
                IndexRow(
                    number = spec.number,
                    name = spec.name,
                    onClick = { onSelect(20) },
                )
            }
        }
    }
}

@Composable
private fun IndexRow(number: Int, name: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "%02d".format(number),
            style = MullionTextStyles.MonoSmall.copy(fontSize = 9.sp),
            color = MullionColors.Volt,
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = name,
            style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
            color = MullionColors.Ink,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = "→",
            style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
            color = MullionColors.Faint,
        )
    }
}
