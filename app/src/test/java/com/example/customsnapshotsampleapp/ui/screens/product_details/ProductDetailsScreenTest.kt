package com.example.customsnapshotsampleapp.ui.screens.product_details

import com.example.core_test.AllureRobolectricRunner
import com.example.core_test.ScreenshotTestRule
import com.example.roborazzisampleapp.ui.screens.products.MockProducts
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
class ProductDetailsScreenTest {

    @get:Rule
    val screenshotTestRule = ScreenshotTestRule()

    @Test
    fun productDetailsScreen() {
        screenshotTestRule.setContent {
            ProductDetailsScreen(product = MockProducts.get(5))
        }
        screenshotTestRule.snapshot()
    }
}
