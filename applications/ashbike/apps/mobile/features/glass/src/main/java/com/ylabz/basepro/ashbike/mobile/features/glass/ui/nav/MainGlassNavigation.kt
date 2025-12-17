package com.ylabz.basepro.ashbike.mobile.features.glass.ui.nav

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.glimmer.Text
import kotlinx.coroutines.launch

@Composable
fun MainGlassNavigation(
    modifier: Modifier = Modifier,
    currentGear: Int,
    onGearChange: (Int) -> Unit,
    onOpenGearList: () -> Unit,
    onClose: () -> Unit,
    // repository: GlassBikeRepository,
) {
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // FOCUS & STATE
    val pagerFocus = remember { FocusRequester() }
    var isInteracting by remember { mutableStateOf(false) }

    // COLOR PALETTE BASED ON MODE
    val modeColor = if (isInteracting) Color.Green else Color.DarkGray
    val modeLabel = if (isInteracting) "CONTROL MODE" else "BROWSING MODE"

    // DYNAMIC INSTRUCTIONS (Crucial for learning curve)
    val hintText = if (isInteracting) {
        "Swipe to Select • Tap to Click • Swipe Down to Exit"
    } else {
        "Swipe to Navigate • Tap to Control"
    }

    LaunchedEffect(Unit) { pagerFocus.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // VISUAL: Add a border when Interacting so user knows they are locked in
            .border(
                width = if (isInteracting) 4.dp else 0.dp,
                color = modeColor,
                shape = RoundedCornerShape(16.dp)
            )
            .focusRequester(pagerFocus)
            .focusable()
            .onKeyEvent { event ->
                // (Keep your existing Key Logic exactly the same)
                if (isInteracting) return@onKeyEvent false
                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            val next = (pagerState.currentPage + 1).coerceAtMost(pageCount - 1)
                            if (next != pagerState.currentPage) {
                                coroutineScope.launch { pagerState.animateScrollToPage(next) }
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                return@onKeyEvent true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                            if (prev != pagerState.currentPage) {
                                coroutineScope.launch { pagerState.animateScrollToPage(prev) }
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                return@onKeyEvent true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            isInteracting = true
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {
        // --- CONTENT LAYER ---
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize().padding(top = 40.dp) // Make room for Status Bar
        ) { pageIndex ->
            GlassPageWrapper(
                isActivePage = (pagerState.currentPage == pageIndex),
                isInteracting = isInteracting,
                onStepOut = {
                    isInteracting = false
                    pagerFocus.requestFocus()
                }
            ) { pageFocus ->
                when (pageIndex) {
                    0 -> NotificationsPage(pageFocus)
                    1 -> DashboardPage(pageFocus)
                    2 -> SettingsPage(pageFocus)
                }
            }
        }

        // --- UI OVERLAY: STATUS BAR (Top) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(modeColor.copy(alpha = 0.2f)) // Subtle background tint
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Current Mode
            Text(
                text = modeLabel,
                color = modeColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            // Right: Helpful Hint
            Text(
                text = hintText,
                color = Color.LightGray,
                fontSize = 10.sp
            )
        }

        // --- UI OVERLAY: PAGE DOTS (Bottom) ---
        // Hide dots when interacting to reduce clutter
        if (!isInteracting) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.White else Color.DarkGray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }
        }
    }
}
// --- Placeholder Composable for context ---
@Composable
fun DashboardPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text("DASHBOARD", color = Color.Green)
    }
}
@Composable
fun NotificationsPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text("NOTIFICATIONS", color = Color.Yellow)
    }
}
@Composable
fun SettingsPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text("SETTINGS", color = Color.Cyan)
    }
}