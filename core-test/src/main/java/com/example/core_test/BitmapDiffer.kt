package com.example.core_test

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.get
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.set
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

fun interface BitmapDiffer {

    fun diff(
        expected: Bitmap,
        actual: Bitmap,
    ): DiffResult

    sealed interface DiffResult {
        val description: String
        val highlights: Bitmap?

        data class Similar(
            override val description: String,
            override val highlights: Bitmap? = null,
        ) : DiffResult

        data class Different(
            override val description: String,
            override val highlights: Bitmap,
        ) : DiffResult
    }

    @Suppress("ComplexCondition")
    object IgnoreAntialiasing : BitmapDiffer {

        override fun diff(
            expected: Bitmap,
            actual: Bitmap,
        ): DiffResult {

            if (expected.sameAs(actual)) {
                return DiffResult.Similar("0 of ${actual.width * actual.height} pixels different")
            }
            var diffPixelCount = 0
            var diffAntialiasedPixelCount = 0
            val expectedHeight = actual.height
            val expectedWidth = actual.width
            val actualHeight = expected.height
            val actualWidth = expected.width
            val height = maxOf(expectedHeight, actualHeight)
            val width = maxOf(expectedWidth, actualWidth)
            val areSizesEquals = expectedWidth == actualWidth && expectedHeight == actualHeight

            val highlights = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                for (y in 0 until height) {

                    val inExpected = x in 0 until expectedWidth && y in 0 until expectedHeight
                    val inActual = x in 0 until actualWidth && y in 0 until actualHeight

                    highlights[x, y] = when {
                        inExpected && inActual -> {
                            val expectedPixel = expected[x, y]
                            val actualPixel = actual[x, y]

                            fun isBrightnessDiff() =
                                abs(expectedPixel.brightness - actualPixel.brightness) > THRESHOLD_PIXEL_DIFF

                            fun isAntialiased() =
                                isAntialiased(actual, expected, x, y) || isAntialiased(
                                    expected,
                                    actual,
                                    x,
                                    y
                                )

                            when {
                                expectedPixel == actualPixel -> {
                                    ColorUtils.setAlphaComponent(
                                        expectedPixel,
                                        (expectedPixel.alpha * 0.5f).toInt()
                                    )
                                }

                                !isBrightnessDiff() || (areSizesEquals && isAntialiased()) -> {
                                    diffAntialiasedPixelCount++
                                    Color.YELLOW
                                }

                                else -> {
                                    diffPixelCount++
                                    Color.RED
                                }
                            }
                        }

                        else -> {
                            diffPixelCount++
                            Color.RED
                        }
                    }
                }
            }
            val description = buildString {
                append(diffPixelCount)
                append(" of ")
                append(width * height)
                append(" pixels different")
                if (diffAntialiasedPixelCount > 0) {
                    append(" (")
                    append(diffAntialiasedPixelCount)
                    append(" ignored because look like some anti-aliasing)")
                }
            }
            return if (diffPixelCount > THRESHOLD_PIXELS_COUNT) {
                DiffResult.Different(description, highlights)
            } else {
                DiffResult.Similar(description, highlights)
            }
        }

        // check if a pixel is likely a part of anti-aliasing
        private fun isAntialiased(
            bitmap1: Bitmap,
            bitmap2: Bitmap,
            x1: Int,
            y1: Int,
        ): Boolean {
            val width = bitmap1.width
            val height = bitmap1.height
            val x0 = max(x1 - 1, 0)
            val y0 = max(y1 - 1, 0)
            val x2 = min(x1 + 1, width - 1)
            val y2 = min(y1 + 1, height - 1)
            var zeroes = if (x1 == x0 || x1 == x2 || y1 == y0 || y1 == y2) 1 else 0
            var min = 0f
            var max = 0f
            var minX = Int.MAX_VALUE
            var minY = Int.MAX_VALUE
            var maxX = Int.MIN_VALUE
            var maxY = Int.MIN_VALUE

            // go through 8 adjacent pixels
            for (x in x0..x2) {
                for (y in y0..y2) {
                    if (x == x1 && y == y1) continue

                    // brightness delta between the center pixel and adjacent one
                    val delta = bitmap1[x1, y1].brightness - bitmap1[x, y].brightness

                    // count the number of equal, darker and brighter adjacent pixels
                    if (delta == 0f) {
                        zeroes++
                        // if found at least ENOUGH_SIBLINGS_COUNT equal siblings, it's definitely not anti-aliasing
                        if (zeroes >= ENOUGH_SIBLINGS_COUNT) return false

                        // remember the darkest pixel
                    } else if (delta < min) {
                        min = delta
                        minX = x
                        minY = y

                        // remember the brightest pixel
                    } else if (delta > max) {
                        max = delta
                        maxX = x
                        maxY = y
                    }
                }
            }

            // if there are no both darker and brighter pixels among siblings, it's not anti-aliasing
            if (min == 0f || max == 0f) return false

            // if either the darkest or the brightest pixel has 3+ equal siblings in both images
            // (definitely not anti-aliased), this pixel is anti-aliased
            return hasManySiblings(bitmap1, minX, minY) && hasManySiblings(bitmap2, minX, minY) ||
                    hasManySiblings(bitmap1, maxX, maxY) && hasManySiblings(bitmap2, maxX, maxY)
        }

        // check if a pixel has 3+ adjacent pixels of the same color.
        private fun hasManySiblings(
            bitmap: Bitmap,
            x1: Int,
            y1: Int,
        ): Boolean {
            val width = bitmap.width
            val x0 = max(x1 - 1, 0)
            val y0 = max(y1 - 1, 0)
            val x2 = min(x1 + 1, width - 1)
            val y2 = min(y1 + 1, bitmap.height - 1)
            var zeroes = if (x1 == x0 || x1 == x2 || y1 == y0 || y1 == y2) 1 else 0
            fun getPixel(
                x: Int,
                y: Int,
            ): Int = bitmap.getPixel(x, y)

            // go through 8 adjacent pixels
            for (x in x0..x2) {
                for (y in y0..y2) {
                    if (x == x1 && y == y1) continue
                    if (getPixel(x1, y1) == getPixel(x, y)) zeroes++
                    if (zeroes >= ENOUGH_SIBLINGS_COUNT) return true
                }
            }

            return false
        }

        private val Int.brightness: Float
            get() {
                val alpha = alpha
                return if (alpha < 255) {
                    val alphaFraction = alpha / 255f
                    rgbToY(
                        blend(red, alphaFraction),
                        blend(green, alphaFraction),
                        blend(blue, alphaFraction)
                    )
                } else {
                    rgbToY(red, green, blue)
                }
            }

        @Suppress("MagicNumber")
        private fun rgbToY(
            red: Int,
            green: Int,
            blue: Int,
        ): Float = red * 0.29889531f + green * 0.58662247f + blue * 0.11448223f

        @Suppress("MagicNumber")
        private fun blend(
            color: Int,
            alpha: Float,
        ): Int = (255 + (color.toFloat() - 255) * alpha).roundToInt()

        private const val THRESHOLD_PIXEL_DIFF = 2f
        private const val THRESHOLD_PIXELS_COUNT = 5
        private const val ENOUGH_SIBLINGS_COUNT = 3
    }
}
