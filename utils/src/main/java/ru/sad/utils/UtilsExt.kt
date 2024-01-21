package ru.sad.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sad.utils.snap.OnSnapPositionChangeListener
import ru.sad.utils.snap.SnapOnScrollListener
import java.io.File
import java.io.FileNotFoundException
import java.io.OutputStream
import java.io.Serializable
import kotlin.Triple

fun Uri.getCameraPhotoOrientation(context: Context): Pair<Uri, Int> {
    var rotate = 0
    try {
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
    } catch (e: Exception) {
    }
    return Pair(this, rotate)
}

fun Pair<Uri, Int>.rotateImage(context: Context) {
    if (this.second == 0) return

    try {
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, this.first)
        val matrix = Matrix()
        matrix.postRotate(this.second.toFloat())

        val bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val os: OutputStream? = context.contentResolver.openOutputStream(this.first)
        os?.let { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    } catch (e: Exception) {
    }
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

fun Uri.copyToFile(context: Context): File {
    val current = context.contentResolver.openInputStream(this)
    val root =
        File(
            context.applicationContext.filesDir,
            ""
        )

    root.mkdir()

    val fileName = "img_" + System.currentTimeMillis() + ".jpg"
    val sdImageMainDirectory = File(root, fileName)

    current?.copyTo(sdImageMainDirectory.outputStream())

    return sdImageMainDirectory
}

fun Int.toSp() = this / Resources.getSystem().displayMetrics.scaledDensity

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


inline fun Fragment.runOnUiThread(crossinline run: () -> Unit = {}) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            run.invoke()
        } catch (e: Exception) {
        }
    }
}

inline fun Fragment.runOnIoThread(
    crossinline run: () -> Unit = {},
    crossinline error: () -> Unit = {}
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            run.invoke()
        } catch (e: Exception) {
            error.invoke()
        }
    }
}

fun RecyclerView.attachSnapHelperWithListener(
    snapHelper: SnapHelper,
    behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
    onSnapPositionChangeListener: OnSnapPositionChangeListener
) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener =
        SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

data class Tuplet<A, B, C>(
    public var first: A,
    public var second: B,
    public var third: C
) : Serializable {

    /**
     * Returns string representation of the [Triple] including its [first], [second] and [third] values.
     */
    public override fun toString(): String = "($first, $second, $third)"
}

data class Four<A, B, C, D>(
    public var first: A,
    public var second: B,
    public var third: C,
    public var fourth: D
) : Serializable {

    /**
     * Returns string representation of the [Triple] including its [first], [second] and [third] values.
     */
    public override fun toString(): String = "($first, $second, $third)"
}

fun <A, B, C> Tuplet<A, B, C>.toTriple(): Triple<A, B, C> {
    return Triple(first, second, third)
}

fun Triple<Float, Float, Float>.sum(): Float {
    return first + second + third
}

