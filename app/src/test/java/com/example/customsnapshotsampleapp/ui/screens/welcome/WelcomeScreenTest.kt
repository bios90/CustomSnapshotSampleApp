package com.example.customsnapshotsampleapp.ui.screens.welcome

import com.example.core_test.AllureRobolectricRunner
import com.example.core_test.ScreenshotTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@Config(
    instrumentedPackages = ["androidx.loader.content"],
    qualifiers = "w340dp-h720dp-xxxhdpi",
)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@RunWith(AllureRobolectricRunner::class)
class WelcomeScreenTest {

    @get:Rule
    val screenshotTestRule = ScreenshotTestRule()

    @Test
    fun welcomeScreen() {
        screenshotTestRule.setContent {
            WelcomeScreen()
        }
        screenshotTestRule.snapshot()
    }

}
