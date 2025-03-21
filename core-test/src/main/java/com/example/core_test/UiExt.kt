@file:SuppressLint("RestrictedApi")
@file:SuppressWarnings("NewApi")

package com.example.core_test

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.concurrent.futures.ResolvableFuture

fun Context.getActivity(): Activity {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> this.baseContext.getActivity()
        else -> throw IllegalStateException(
            "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
                    "An Activity context is required to get a Window instance"
        )
    }
}

fun Context.getWindow(): Window = getActivity().window

fun View.generateBitmap(): Bitmap {
    val bitmapFuture: ResolvableFuture<Bitmap> = ResolvableFuture.create()
    val destBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val window = this.context.getWindow()
    generateBitmapFromPixelCopy(window, destBitmap, bitmapFuture)
    return bitmapFuture.get()
}

private fun View.generateBitmapFromPixelCopy(
    window: Window,
    destBitmap: Bitmap,
    bitmapFuture: ResolvableFuture<Bitmap>,
) {
    val locationInWindow = intArrayOf(0, 0)
    getLocationInWindow(locationInWindow)
    val x = locationInWindow[0]
    val y = locationInWindow[1]
    val boundsInWindow = Rect(x, y, x + width, y + height)

    return window.generateBitmapFromPixelCopy(boundsInWindow, destBitmap, bitmapFuture)
}

private fun Window.generateBitmapFromPixelCopy(
    boundsInWindow: Rect? = null,
    destBitmap: Bitmap,
    bitmapFuture: ResolvableFuture<Bitmap>,
) {
    val onCopyFinished =
        PixelCopy.OnPixelCopyFinishedListener { result ->
            if (result == PixelCopy.SUCCESS) {
                bitmapFuture.set(destBitmap)
            } else {
                bitmapFuture.setException(
                    RuntimeException(
                        String.format(
                            "PixelCopy failed: %d",
                            result
                        )
                    )
                )
            }
        }
    PixelCopy.request(
        this,
        boundsInWindow,
        destBitmap,
        onCopyFinished,
        Handler(Looper.getMainLooper())
    )
}
