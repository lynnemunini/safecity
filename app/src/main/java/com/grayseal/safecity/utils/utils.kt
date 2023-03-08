package com.grayseal.safecity.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

// Extension function to convert a Drawable to a BitmapDrawable
fun Drawable.toBitmapDrawable(context: Context): BitmapDrawable {
    val bitmap = Bitmap.createBitmap(
        intrinsicWidth.takeIf { it > 0 } ?: 1,
        intrinsicHeight.takeIf { it > 0 } ?: 1,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return BitmapDrawable(context.resources, bitmap)
}

// Extension function to convert a BitmapDrawable to an ImageBitmap
fun BitmapDrawable.toImageBitmap(): ImageBitmap {
    return this.bitmap.asImageBitmap()
}