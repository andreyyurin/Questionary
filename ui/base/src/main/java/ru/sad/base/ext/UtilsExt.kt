package ru.sad.base.ext

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import androidx.annotation.CheckResult
import androidx.annotation.MainThread
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.core.net.toFile
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import ru.sad.domain.model.simple.SimpleTypeScreenEnum
import java.io.File
import java.io.OutputStream
import java.lang.Integer.max
import java.security.AccessController.getContext


private const val MAX_WIDTH = 500
private const val MAX_HEIGHT = 500

fun Uri.getCameraPhotoOrientation(context: Context): Pair<Uri, Int> {
    var rotate = 0
    val inputStream = context.contentResolver.openInputStream(this)

    val exif = ExifInterface(inputStream!!)
    when (exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )) {
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
    }
    inputStream.close()
    return Pair(this, rotate)
}

fun Pair<Uri, Int>.rotateImage(context: Context) {
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, this.first)
    val matrix = Matrix()
    matrix.postRotate(this.second.toFloat())

    val bmp = if (bitmap.width > bitmap.height) {
        val scaleHeight = MAX_WIDTH.toFloat() / bitmap.width.toFloat()

        val newHeight = (bitmap.height * scaleHeight).toInt()

        Bitmap.createScaledBitmap(
            bitmap,
            MAX_WIDTH,
            newHeight,
            true
        )
    } else {
        val scaleWidth = MAX_HEIGHT.toFloat() / bitmap.height.toFloat()

        val newWidth = (bitmap.width * scaleWidth).toInt()

        Bitmap.createScaledBitmap(
            bitmap,
            newWidth,
            MAX_HEIGHT,
            true
        )
    }

    val bmpResult = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    val os: OutputStream? = context.contentResolver.openOutputStream(this.first)
    os?.let { bmpResult.compress(Bitmap.CompressFormat.PNG, 100, it) }
}

fun Uri.copyToFile(context: Context): File {
    val current = context.contentResolver.openInputStream(this)
    val root =
        File(
            context.applicationContext.filesDir,
            context.getString(ru.sad.base.R.string.photos_dirname)
        )

    root.mkdir()

    val fileName = "img_" + System.currentTimeMillis() + ".jpg"
    val sdImageMainDirectory = File(root, fileName)

    current?.copyTo(sdImageMainDirectory.outputStream())

    return sdImageMainDirectory
}

fun Int.toSp() = this / Resources.getSystem().displayMetrics.scaledDensity

val Number.toSp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

val Number.toDp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_PX,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

//@JvmName("map")
//@MainThread
//@CheckResult
//fun <X, Y> LiveData<X>.mapWithScreenType(
//    type: SimpleTypeScreenEnum,
//    transform: (@JvmSuppressWildcards X) -> (@JvmSuppressWildcards Y)
//): LiveData<Y> {
//    val result = MediatorLiveData<Y>()
//    val castedResult = result.value as? Pair<*, *>
//
//    if (castedResult?.first is SimpleTypeScreenEnum && castedResult.first == type) {
//        result.addSource(this) { x ->
//            result.postValue(transform(x))
//        }
//    }
//    return result
//}

@JvmName("map")
@MainThread
@CheckResult
inline fun <X, reified Y> MutableLiveData<X>.mapWithScreenType(
    type: SimpleTypeScreenEnum,
): MutableLiveData<Y> {
    val result = MediatorLiveData<Y>()

    result.addSource(this) { x ->
        val castedData = x as? Pair<*, *>

        if(castedData?.first is SimpleTypeScreenEnum && castedData.first == type && castedData.second is Y) {
            result.postValue(castedData.second as Y)
        }
    }
    return result
}