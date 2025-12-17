package com.ylabz.basepro.ashbike.mobile.features.glass.ui.nav

import android.view.KeyEvent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MainGlassNavigation() {
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val coroutineScope = rememberCoroutineScope()

    // 1. Haptics: Critical for "eyes-up" interaction
    val haptics = LocalHapticFeedback.current

    // 2. Focus: Required to capture hardware temple swipes
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // High contrast background
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    // Map standard Glass D-Pad keys to navigation
                    val direction = when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_RIGHT -> 1
                        KeyEvent.KEYCODE_DPAD_LEFT -> -1
                        else -> 0
                    }

                    if (direction != 0) {
                        val targetPage = (pagerState.currentPage + direction)
                            .coerceIn(0, pageCount - 1)

                        if (targetPage != pagerState.currentPage) {
                            // Feedback: Feel the click
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)

                            coroutineScope.launch {
                                // Animation: Fast "Snap" to avoid motion sickness
                                pagerState.animateScrollToPage(
                                    targetPage,
                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                )
                            }
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {
        // --- CONTENT ---
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false, // DISABLE touch dragging (it fails on Glass)
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            when (pageIndex) {
                0 -> NotificationsPage() // Left
                1 -> DashboardPage()     // Center (Start here normally)
                2 -> SettingsPage()      // Right
            }
        }

        // --- INDICATOR (The "Dots") ---
        // Crucial so the user knows where they are relative to the center
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