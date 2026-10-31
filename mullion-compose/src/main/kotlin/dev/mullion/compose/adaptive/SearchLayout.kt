package dev.mullion.compose.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.GlassLevel
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles
import dev.mullion.compose.theme.glass
import dev.mullion.compose.window.mullionWindow
import dev.mullion.core.PaneSlot
import dev.mullion.core.PaneStrategy

/**
 * A glass search field with a focus ring and a clear affordance.
 *
 * It is deliberately free of navigation concerns: place it in a toolbar or above
 * [SearchLayout].
 */
@Composable
public fun MullionSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索组件 / 断点 / 设备…",
    onSearch: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current
    val borderColor by animateColorAsState(
        targetValue = if (focused) MullionColors.Volt.copy(alpha = 0.7f) else MullionColors.Edge,
        label = "SearchBar.border",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .glass(shape = CircleShape, level = GlassLevel.Chip)
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchGlyph()
            Spacer(Modifier.width(9.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MullionTextStyles.MonoSmall.copy(fontSize = 11.sp),
                        color = MullionColors.Faint,
                        maxLines = 1,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MullionTextStyles.Mono.copy(fontSize = 12.sp),
                    singleLine = true,
                    cursorBrush = SolidColor(MullionColors.Volt),
                    interactionSource = interaction,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            onSearch?.invoke()
                        },
                    ),
                )
            }
            if (value.isNotEmpty()) {
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MullionColors.Tonal, CircleShape)
                        .clickable { onValueChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "×",
                        style = MullionTextStyles.Mono,
                        color = MullionColors.Muted,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchGlyph() {
    Canvas(modifier = Modifier.size(15.dp)) {
        drawCircle(
            color = MullionColors.Muted,
            radius = size.minDimension * 0.32f,
            center = Offset(size.width * 0.42f, size.height * 0.42f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6.dp.toPx()),
        )
        drawLine(
            color = MullionColors.Muted,
            start = Offset(size.width * 0.64f, size.height * 0.64f),
            end = Offset(size.width * 0.9f, size.height * 0.9f),
            strokeWidth = 1.6.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

/**
 * Search results that grow a preview pane and then a filter rail.
 *
 *  - one pane: the result list only
 *  - two panes: results + preview (or filters when no preview is supplied)
 *  - three panes: results + preview + filters
 *
 * Query state lives with the caller, so collapsing the window never clears the
 * search.
 */
@Composable
public fun SearchLayout(
    query: String,
    onQueryChange: (String) -> Unit,
    results: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "搜索设备 / 型号 / 断点…",
    preview: (@Composable () -> Unit)? = null,
    filters: (@Composable () -> Unit)? = null,
    resultCount: Int? = null,
    strategy: PaneStrategy = PaneStrategy(),
    onSearch: (() -> Unit)? = null,
) {
    val window = mullionWindow()
    val slots = buildList {
        add(PaneSlot.List)
        if (preview != null) add(PaneSlot.Preview)
        if (filters != null) add(PaneSlot.Filters)
    }
    val plan = strategy.plan(window.bp, slots)
    val gutter = plan.gutterDp.dp

    Column(modifier = modifier.fillMaxSize()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MullionSearchBar(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = placeholder,
                onSearch = onSearch,
            )
            if (resultCount != null) {
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "$resultCount RESULTS",
                    style = MullionTextStyles.LabelSmall,
                    color = MullionColors.Amber,
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val previewWidth = 300.dp
            val filtersWidth = 220.dp
            val minimumResultsWidth = 320.dp

            // Same rule as MultiPane: the content width, not the window class,
            // decides whether a trailing rail can survive.
            val showPreview = preview != null &&
                plan.shows(PaneSlot.Preview) &&
                maxWidth >= minimumResultsWidth + previewWidth + gutter
            val showFilters = filters != null &&
                plan.shows(PaneSlot.Filters) &&
                maxWidth >= minimumResultsWidth +
                (if (showPreview) previewWidth + gutter else 0.dp) +
                filtersWidth + gutter

            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f).fillMaxSize()) { results() }
                if (showPreview) {
                    PaneDivider(gutter)
                    Box(modifier = Modifier.width(previewWidth).fillMaxSize()) { preview() }
                }
                if (showFilters) {
                    PaneDivider(gutter)
                    Box(modifier = Modifier.width(filtersWidth).fillMaxSize()) { filters() }
                }
            }
        }
    }
}
