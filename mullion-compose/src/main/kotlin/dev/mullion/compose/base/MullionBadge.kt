package dev.mullion.compose.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTextStyles

/** Visual kind of a badge. */
public enum class MullionBadgeKind {
    Dot,
    Number,
    Text,
}

/**
 * A badge attached to any content: dot, number, or short text.
 *
 * Numbers above 99 collapse to `99+`. The badge is drawn in the top-end corner
 * and offset outward so it never covers the content's own glyph.
 */
@Composable
public fun MullionBadge(
    modifier: Modifier = Modifier,
    count: Int? = null,
    text: String? = null,
    kind: MullionBadgeKind = when {
        text != null -> MullionBadgeKind.Text
        count != null -> MullionBadgeKind.Number
        else -> MullionBadgeKind.Dot
    },
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        val offsetX = if (kind == MullionBadgeKind.Text) 6.dp else 4.dp
        val offsetY = if (kind == MullionBadgeKind.Text) (-6).dp else (-4).dp
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = offsetX, y = offsetY)
                .then(
                    when (kind) {
                        MullionBadgeKind.Dot -> Modifier
                            .size(11.dp)
                            .background(MullionColors.Signal, CircleShape)
                            .border(
                                width = 2.dp,
                                color = MullionColors.Background.copy(alpha = 0.9f),
                                shape = CircleShape,
                            )

                        MullionBadgeKind.Number -> Modifier
                            .defaultMinSize(minWidth = 20.dp)
                            .height(20.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(MullionColors.VoltHigh, MullionColors.Volt),
                                ),
                                CircleShape,
                            )
                            .border(2.dp, MullionColors.Background.copy(alpha = 0.9f), CircleShape)
                            .padding(horizontal = 5.dp)

                        MullionBadgeKind.Text -> Modifier
                            .height(18.dp)
                            .background(
                                Brush.verticalGradient(
                                    listOf(MullionColors.Amber, MullionColors.Signal),
                                ),
                                RoundedCornerShape(999.dp),
                            )
                            .border(2.dp, MullionColors.Background.copy(alpha = 0.9f), CircleShape)
                            .padding(horizontal = 7.dp)
                    },
                )
                .semantics {
                    if (contentDescription != null) this.contentDescription = contentDescription
                },
            contentAlignment = Alignment.Center,
        ) {
            when (kind) {
                MullionBadgeKind.Dot -> Unit
                MullionBadgeKind.Number -> Text(
                    text = if ((count ?: 0) > 99) "99+" else (count ?: 0).toString(),
                    style = MullionTextStyles.UISmall.copy(fontSize = 9.sp),
                    color = MullionColors.OnVolt,
                )

                MullionBadgeKind.Text -> Text(
                    text = text.orEmpty(),
                    style = MullionTextStyles.UISmall.copy(fontSize = 8.5.sp),
                    color = MullionColors.Background,
                )
            }
        }
    }
}
