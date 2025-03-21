package com.example.core_test

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.view.drawToBitmap
import io.qameta.allure.kotlin.Allure.attachment
import kotlinx.coroutines.test.runTest
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.ByteArrayOutputStream
import java.io.File

class ScreenshotTestRule(
    private val testMode: TestMode = TestMode.COMPARE,
    private val captureMode: CaptureMode = CaptureMode.PIXEL_COPY,
) : TestRule {

    enum class CaptureMode {
        DRAW_TO_BITMAP,
        PIXEL_COPY
    }

    enum class TestMode {
        RECORD,
        COMPARE
    }

    private var testContent: View? = null
    private val testClassInfoRule: TestClassInfoRule = TestClassInfoRule()
    private val composeContentTestRule: ComposeContentTestRule = createComposeRule()

    override fun apply(
        base: Statement?,
        description: Description?,
    ): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    RuleChain.outerRule(testClassInfoRule)
                        .around(composeContentTestRule)
                        .apply(base, description)
                        .evaluate()
                } finally {
                    testContent = null
                }
            }
        }
    }

    fun setContent(composable: @Composable () -> Unit) {
        composeContentTestRule.setContent {
            testContent = LocalView.current
            composable()
        }
        runTest { composeContentTestRule.awaitIdle() }
    }

    fun snapshot() {
        val bitmap = requireNotNull(getTestContentBitmap())
        val files = ScreenshotFiles(
            referenceDirectoryName = "src/test/screenshots",
            diffDirectoryName = "build/screenshots/diff",
            outputDirectoryName = "build/screenshots/output",
            filePrefix = testClassInfoRule.getFileNamePrefix()
        )
        when (testMode) {
            TestMode.RECORD -> files.record(bitmap)
            TestMode.COMPARE -> files.compare(bitmap)
        }
    }

    private fun getTestContentBitmap(): Bitmap? = when (captureMode) {
        CaptureMode.DRAW_TO_BITMAP -> testContent?.drawToBitmap()
        CaptureMode.PIXEL_COPY -> testContent?.generateBitmap()
    }
}

private class TestClassInfoRule : TestWatcher() {

    @Volatile
    var methodName: String? = null
        private set

    @Volatile
    var testClass: Class<*>? = null
        private set

    override fun starting(d: Description) {
        methodName = d.methodName
        testClass = d.testClass
    }
}

@SuppressLint("NewApi")
private fun TestClassInfoRule.getFileNamePrefix(): String {
    val testClass = checkNotNull(testClass) { "Could not retrieve information from test class" }
    val testName = methodName
    val packageName = testClass.packageName
    val className = testClass.simpleName
    return packageName + "_" + className + "_" + testName
}

private class ScreenshotFiles(
    outputDirectoryName: String,
    referenceDirectoryName: String,
    diffDirectoryName: String,
    filePrefix: String,
) {
    val referenceDirectory = File(referenceDirectoryName).apply { mkdirs() }
    val diffDirectory = File(diffDirectoryName)
    val outputDirectory = File(outputDirectoryName)
    val expectedFile = File(referenceDirectory, "$filePrefix.png")
    val actualFile = File(outputDirectoryName, "$filePrefix.png")
    val diffFile = File(diffDirectory, "${filePrefix}_diff.png")
}

private fun ScreenshotFiles.record(actualBitmap: Bitmap) {
    expectedFile.writeBitmapAsPng(actualBitmap)
    diffFile.deleteRecursively()
    println("Stored screenshot to: ${expectedFile.absolutePath}")
    actualBitmap.attachAsPng("Recorded")
    actualBitmap.recycle()
    val message = buildString {
        append("Record enabled. Saved new reference screenshot:")
        append("\n")
        append("file://${expectedFile.absolutePath}")
    }
    throw AssertionError(message)
}

private fun ScreenshotFiles.compare(actualBitmap: Bitmap) {
    val expectedBitmap = expectedFile.readBitmap()
    val diffResult =
        BitmapDiffer.IgnoreAntialiasing.diff(expected = expectedBitmap, actual = actualBitmap)
    attachment("Name", expectedFile.name)
    attachment("Result", diffResult.description)
    try {
        when (diffResult) {
            is BitmapDiffer.DiffResult.Similar -> {
                diffFile.deleteRecursively()
                actualFile.deleteRecursively()
                diffResult.highlights?.attachAsPng("Diff") ?: actualBitmap.attachAsPng("Actual")
            }

            is BitmapDiffer.DiffResult.Different -> {
                diffDirectory.mkdirs()
                outputDirectory.mkdirs()
                diffFile.writeBitmapAsPng(diffResult.highlights)
                actualFile.writeBitmapAsPng(actualBitmap)
                expectedBitmap.attachAsPng("Expected")
                actualBitmap.attachAsPng("Actual")
                diffResult.highlights.attachAsPng("Diff")
                val message = buildString {
                    append("Screenshot is different from the reference!")
                    append("\n")
                    append(diffResult.description)
                    append("\n")
                    append("Expected: file://${expectedFile.absolutePath}")
                    append("\n")
                    append("Actual: file://${actualFile.absolutePath}")
                    append("\n")
                    append("Diff: file://${diffFile.absolutePath}")
                }
                throw AssertionError(message)
            }
        }
    } finally {
        actualBitmap.recycle()
        expectedBitmap.recycle()
        diffResult.highlights?.recycle()
    }
}

@Suppress("TooGenericExceptionCaught", "SwallowedException")
private fun File.writeBitmapAsPng(bitmap: Bitmap) {
    try {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        val byteArray = outputStream.toByteArray()
        writeBytes(byteArray)
    } catch (error: Throwable) {
        throw AssertionError("Couldn't store bitmap: $bitmap to file: $this")
    }
}

@Suppress("TooGenericExceptionCaught", "SwallowedException")
private fun File.readBitmap(): Bitmap =
    try {
        requireNotNull(BitmapFactory.decodeFile(absolutePath))
    } catch (error: Throwable) {
        throw AssertionError("Couldn't load: $this")
    }

private fun Bitmap.attachAsPng(name: String) {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 0, outputStream)
    attachment(
        name = name,
        type = "image/png",
        fileExtension = ".png",
        content = outputStream.toByteArray().inputStream()
    )
}
