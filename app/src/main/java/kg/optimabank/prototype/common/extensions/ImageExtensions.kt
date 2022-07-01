package kg.optimabank.prototype.common.extensions

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64(): String {
    val byteStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 90, byteStream)
    val byteArray = byteStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}