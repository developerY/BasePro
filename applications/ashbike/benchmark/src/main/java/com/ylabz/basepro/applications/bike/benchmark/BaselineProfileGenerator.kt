package com.ylabz.basepro.applications.bike.benchmark // Or your chosen package for this class

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a baseline profile which can be copied to
 * `applications/ashbike/src/main/baseline-prof.txt`.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(
        // THIS IS THE IMPORTANT PART:
        // Replace "com.ylabz.basepro.applications.bike" with the actual applicationId
        // of your ashbike application module. You can find this in your
        // applications/ashbike/build.gradle.kts file (look for `applicationId`).
        // namespace = "com.ylabz.basepro.applications.bike"
        packageName = "com.ylabz.basepro.applications.bike", // <<< --- Correct!!!

        // Keep default modes for Baseline Profile generation.
        // BaselineProfileMode.Require ensures the profile is written to a txt file in the output.
        // CompilationMode.Partial compiles OSR (On Stack Replacement) methods for warm-up in addition to baseline.
        // mode = BaselineProfileMode.Require, // This is the default mode
        // compilationMode = CompilationMode.Partial(), // This is the default mode

        profileBlock = {
            // This block defines the app's critical user journey.
            // The pressHome() and startActivityAndWait() actions are essential.
            pressHome()
            startActivityAndWait()

            // TODO: Add interactions to wait for content to load and interact with the UI.
            // This is CRITICAL for a useful profile. You need to guide the app
            // through its common startup and usage paths.

            // Example: Wait for a specific UI element to appear after startup
            // device.wait(Until.hasObject(By.res("com.ylabz.basepro.applications.bike:id/your_main_content_id")), 10_000)

            // Example: Scroll a list (if you have one on your main screen)
            // val recycler = device.findObject(By.res("com.ylabz.basepro.applications.bike:id/your_recycler_view_id"))
            // if (recycler != null) {
            //     recycler.setGestureMargin(device.displayWidth / 5)
            //     recycler.fling(Direction.DOWN)
            //     device.waitForIdle() // Wait for scroll to settle
            // }

            // Example: Navigate to another important screen
            // device.findObject(By.text("Settings"))?.click() // Or By.res, By.desc
            // device.wait(Until.hasObject(By.res("com.ylabz.basepro.applications.bike:id/settings_screen_element_id")), 5_000)

            // Add more interactions here to cover your app's critical user journeys (CUJs).
            // Think about what users do most often when they open your app.
        }
    )
}
