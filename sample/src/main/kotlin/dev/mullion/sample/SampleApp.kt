package dev.mullion.sample

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.mullion.compose.theme.MullionColors
import dev.mullion.compose.theme.MullionTheme
import dev.mullion.compose.window.MullionWindowProvider
import dev.mullion.sample.showcase.AdaptiveCard
import dev.mullion.sample.showcase.BaseKitSection
import dev.mullion.sample.showcase.BreakpointLab
import dev.mullion.sample.showcase.BreakpointRail
import dev.mullion.sample.showcase.ClaimSection
import dev.mullion.sample.showcase.FooterSection
import dev.mullion.sample.showcase.HeroSection
import dev.mullion.sample.showcase.IndexOverlay
import dev.mullion.sample.showcase.NamingAndUsage
import dev.mullion.sample.showcase.ViewInteropSection
import dev.mullion.sample.showcase.adaptiveSpecs
import dev.mullion.sample.ui.PageSection
import dev.mullion.sample.ui.SectionHeader
import dev.mullion.sample.ui.SampleBackdrop
import dev.mullion.sample.ui.SampleTopBar
import kotlinx.coroutines.launch

private const val GITHUB_URL = "https://github.com/Mullion-Official/Mullion-Android-Kotlin-Compose"

/**
 * The glass cabinet: a live, scrollable sample of all 26 modules.
 *
 * The page itself is a normal app. Every adaptive tray inside it simulates a
 * different window width, so you can watch layout decisions change without
 * resizing the device.
 */
@Composable
fun SampleApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var indexOpen by remember { mutableStateOf(false) }

    val openGithub = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL))
        runCatching { context.startActivity(intent) }
        Unit
    }

    Box(modifier = Modifier.fillMaxSize().background(MullionColors.Background)) {
        SampleBackdrop()
        MullionWindowProvider {
            Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                PageSection(modifier = Modifier.padding(top = 8.dp)) {
                    SampleTopBar(
                        onAdaptive = {
                            scope.launch { listState.scrollToItem(5) }
                        },
                        onBase = {
                            scope.launch { listState.scrollToItem(20) }
                        },
                        onIndex = { indexOpen = true },
                        onGithub = openGithub,
                    )
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 48.dp),
                ) {
                    item(key = "hero") {
                        PageSection(modifier = Modifier.padding(top = 14.dp)) {
                            HeroSection()
                        }
                    }
                    item(key = "claim") {
                        PageSection(modifier = Modifier.padding(top = 26.dp)) {
                            ClaimSection()
                        }
                    }
                    item(key = "naming") {
                        PageSection(modifier = Modifier.padding(top = 18.dp)) {
                            NamingAndUsage()
                        }
                    }
                    item(key = "rail") {
                        PageSection(modifier = Modifier.padding(top = 14.dp)) {
                            BreakpointRail()
                        }
                    }
                    item(key = "lab") {
                        PageSection(modifier = Modifier.padding(top = 14.dp)) {
                            BreakpointLab()
                        }
                    }
                    item(key = "adaptive-header") {
                        PageSection(modifier = Modifier.padding(top = 44.dp)) {
                            SectionHeader(
                                kicker = "Adaptive Kit / 01–14",
                                title = "14 个自适应件，管「怎么分格」。",
                                description = "每个托盘都是一个真实的隔离子树：375 / 760 / 1180 三个模拟窗口共享同一套断点、栏数、铰链与 inset 判定。缩放只发生在渲染层，布局是真实的。",
                            )
                        }
                    }
                    items(adaptiveSpecs, key = { "c${it.number}" }) { spec ->
                        PageSection(modifier = Modifier.padding(top = 16.dp)) {
                            AdaptiveCard(spec)
                        }
                    }
                    item(key = "base-kit") {
                        PageSection(modifier = Modifier.padding(top = 44.dp)) {
                            BaseKitSection()
                        }
                    }
                    item(key = "view-tier") {
                        PageSection(modifier = Modifier.padding(top = 44.dp)) {
                            ViewInteropSection()
                        }
                    }
                    item(key = "footer") {
                        PageSection(modifier = Modifier.padding(top = 44.dp)) {
                            FooterSection()
                        }
                    }
                }
            }
        }

        IndexOverlay(
            visible = indexOpen,
            onDismiss = { indexOpen = false },
            onSelect = { index ->
                indexOpen = false
                scope.launch { listState.scrollToItem(index) }
            },
        )
    }
}
