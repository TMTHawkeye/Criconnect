package com.example.criconnect.HelperClasses

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import java.io.ByteArrayOutputStream

  fun base64ToDrawable(base64: String?): Drawable? {
    val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
    return BitmapDrawable(Resources.getSystem(), BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size))
}

  fun drawableToBase64(drawable: Drawable?): String {
    val bitmap = (drawable as BitmapDrawable).bitmap
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}