package com.ylabz.basepro.ashbike.mobile.features.glass.ui.nav

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import kotlinx.coroutines.launch

// ----------------------------------------------------------------------------------
// 1. THE MAIN HOST (Pager Logic)
// ----------------------------------------------------------------------------------
@Composable
fun MainGlassNavigation() {
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current

    // "pagerFocus" controls the swipes between screens.
    // When this has focus, Left/Right changes pages.
    val pagerFocus = remember { FocusRequester() }

    // State to track if we are browsing (Pager) or interacting (Page Content)
    var isInteracting by remember { mutableStateOf(false) }

    // Grab focus for the Pager immediately on launch
    LaunchedEffect(Unit) {
        pagerFocus.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(pagerFocus)
            .focusable() // The Pager itself must be focusable to catch keys
            .onKeyEvent { event ->
                if (isInteracting) return@onKeyEvent false // If locked in, ignore keys here

                if (event.type == KeyEventType.KeyDown) {
                    when (event.nativeKeyEvent.keyCode) {
                        // --- NAVIGATION: SWIPE LEFT/RIGHT ---
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            val next = (pagerState.currentPage + 1).coerceAtMost(pageCount - 1)
                            if (next != pagerState.currentPage) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(next, animationSpec = tween(300))
                                }
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                return@onKeyEvent true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            val prev = (pagerState.currentPage - 1).coerceAtLeast(0)
                            if (prev != pagerState.currentPage) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(prev, animationSpec = tween(300))
                                }
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                return@onKeyEvent true
                            }
                        }

                        // --- INTERACTION: TAP / ENTER ---
                        // This triggers the "Step In" mode
                        KeyEvent.KEYCODE_DPAD_CENTER,
                        KeyEvent.KEYCODE_ENTER -> {
                            isInteracting = true
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {
        // PAGER CONTENT
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false, // DISABLE touch dragging
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            // Wrap every page in our logic helper
            GlassPageWrapper(
                isActivePage = (pagerState.currentPage == pageIndex),
                isInteracting = isInteracting,
                onStepOut = {
                    // When user hits "Back" inside the page, we return to Pager mode
                    isInteracting = false
                    pagerFocus.requestFocus()
                }
            ) { pageFocus ->

                // Pass the focus requester down to the actual screen
                when (pageIndex) {
                    0 -> NotificationsPage(pageFocus)
                    1 -> DashboardPage(pageFocus)
                    2 -> SettingsPage(pageFocus)
                }
            }
        }

        // VISUAL INDICATOR (The Dots)
        // Hidden when interacting to reduce clutter? Or keep them?
        // Let's dim them when interacting.
        val dotAlpha = if (isInteracting) 0.3f else 1.0f
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
                        .background(color.copy(alpha = dotAlpha))
                        .size(8.dp)
                )
            }
        }
    }
}

// ----------------------------------------------------------------------------------
// 2. THE WRAPPER (Focus Management Logic)
// ----------------------------------------------------------------------------------
@Composable
fun GlassPageWrapper(
    isActivePage: Boolean,
    isInteracting: Boolean,
    onStepOut: () -> Unit,
    content: @Composable (focusRequester: FocusRequester) -> Unit
) {
    // Each page gets its own requester for its internal content
    val pageContentFocus = remember { FocusRequester() }

    // If this is the active page AND interaction just turned on,
    // force focus onto the content (buttons)
    LaunchedEffect(isInteracting, isActivePage) {
        if (isInteracting && isActivePage) {
            pageContentFocus.requestFocus()
        }
    }

    // Handle "Back" (Swipe Down) to Step Out
    // Only enabled if we are currently interacting
    BackHandler(enabled = isInteracting && isActivePage) {
        onStepOut()
    }

    // Visual Cue: Green border when "Locked In"
    val borderMod = if (isInteracting && isActivePage) {
        Modifier.border(2.dp, Color.Green.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp) // Little padding for the border
            .then(borderMod)
    ) {
        content(pageContentFocus)
    }
}

// ----------------------------------------------------------------------------------
// 3. SAMPLE SCREENS (AshBike Implementation)
// ----------------------------------------------------------------------------------

@Composable
fun DashboardPage(focusRequester: FocusRequester) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("DASHBOARD", color = Color.Gray, fontSize = 12.sp)
        Text("GEAR", color = Color.White, fontSize = 20.sp)
        Text("1", fontSize = 80.sp, color = Color.Green, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            // GEAR DOWN
            // IMPORTANT: The first focusable item needs the focusRequester!
            Button(
                onClick = { /* Logic */ },
                modifier = Modifier
                    .focusRequester(focusRequester) // <--- CRITICAL: Receive focus here
            ) {
                Text("-")
            }

            Spacer(modifier = Modifier.width(24.dp))

            // GEAR UP
            Button(onClick = { /* Logic */ }) {
                Text("+")
            }
        }
    }
}

@Composable
fun NotificationsPage(focusRequester: FocusRequester) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Even if a page has no buttons, it needs to accept focus to prevent crashing
        // if the user accidentally "Steps In"
        Button(
            onClick = {},
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("Clear All")
        }
    }
}

@Composable
fun SettingsPage(focusRequester: FocusRequester) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SETTINGS", color = Color.Cyan)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {},
            modifier = Modifier.focusRequester(focusRequester)
        ) {
            Text("Reset Ride")
        }
    }
}