package ru.sad.base.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.fragment.app.Fragment
import java.io.FileNotFoundException

fun Fragment.getAndroidContentFrameRect(): Rect {
    val rectangle = Rect()
    val window = requireActivity().window
    window.decorView.getWindowVisibleDisplayFrame(rectangle)
    return rectangle
}

@Throws(FileNotFoundException::class)
fun Fragment.decodeUri(uri: Uri, requiredSize: Int): Bitmap {
    val o = BitmapFactory.Options()
    o.inJustDecodeBounds = true
    BitmapFactory.decodeStream(this.requireContext().contentResolver.openInputStream(uri), null, o)
    var tmpWidth = o.outWidth
    var tmpHeight = o.outHeight
    var scale = 1
    while (true) {
        if (tmpWidth / 2 < requiredSize || tmpHeight / 2 < requiredSize) break
        tmpWidth /= 2
        tmpHeight /= 2
        scale *= 2
    }
    val o2 = BitmapFactory.Options()
    o2.inSampleSize = scale
    return BitmapFactory.decodeStream(
        this.requireContext().contentResolver.openInputStream(uri),
        null,
        o2
    )!!
}

fun Fragment.setupFragment(id: Int, fragment: Fragment) {
    childFragmentManager
        .beginTransaction()
        .replace(id, fragment)
        .commit()
}