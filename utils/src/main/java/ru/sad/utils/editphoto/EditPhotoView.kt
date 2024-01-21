package ru.sad.utils.editphoto

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.net.Uri
import android.os.Build.VERSION_CODES.S
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.net.URI
import java.util.Stack

class EditPhotoView : View {

    private var currentImage: Bitmap? = null
    private var originalImage: Bitmap? = null

    private var scaleFactor = 1f

    private lateinit var destCanvas: Canvas

    private var listActions = Stack<Action>()

    private var selectedColor = Color.TRANSPARENT
    private var selectedSize = 50f

    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0) {
        init()
    }

    constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        currentImage?.let {
            drawPathes()
            canvas.scale(scaleFactor, scaleFactor, width.toFloat() / 2, height.toFloat() / 2)
            canvas.drawBitmap(it, ((width - it.width) / 2).toFloat(), 0f, null)
        }

        super.onDraw(canvas)
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val positionX = event.x
        val positionY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentImage?.let {
                    val path = Path()
                    path.moveTo(positionX - ((width - it.width) / 2).toFloat(), positionY)
                    listActions.push(Action(path, createPaint(), true))
                }
            }

            MotionEvent.ACTION_MOVE -> {
                currentImage?.let {
                    listActions.lastOrNull()?.let { action ->
                        action.first.lineTo(
                            positionX - ((width - it.width) / 2).toFloat(),
                            positionY
                        )
                    }
                }
            }

            else -> return false
        }

        invalidate()
        return true
    }

    fun init(image: Bitmap? = null, width: Int = 0, height: Int = 0) {
        currentImage = image?.copy(Bitmap.Config.ARGB_8888, true)
        originalImage = image?.copy(Bitmap.Config.ARGB_8888, true)

        if (width != 0 && height != 0) {
            scaleImage(width, height)
        }

        currentImage?.let {
            destCanvas = Canvas(it)
            destCanvas.drawBitmap(it, 0f, 0f, null)
        }
    }

    fun upScale() {
        if (scaleFactor < 2f) scaleFactor += 0.05f
        invalidate()
    }

    fun downScale() {
        if (scaleFactor > 0.5f) scaleFactor -= 0.05f
        invalidate()
    }

    fun selectColor(@ColorInt color: Int) {
        selectedColor = color
    }

    fun selectPaintSize(size: Int) {
        selectedSize = size.toFloat()
    }

    fun removeLastStep() {
        if (listActions.isEmpty()) return

        listActions.last().first = Path()
        listActions.pop()
        init(originalImage)
        invalidate()
    }

    fun getBitmap(uriStr: String?): Uri? {
        val uri = Uri.parse(uriStr)

        val file = File(URI(uriStr))
        if (file.exists()) {
            file.delete()
        }

        val stream = FileOutputStream(file)
        stream.let { os -> currentImage?.compress(Bitmap.CompressFormat.WEBP, 100, os) }
        stream.flush()
        stream.close()
        return uri
    }


    private fun scaleImage(width: Int, height: Int) {
        val image: Bitmap = currentImage ?: return

        currentImage = if (image.width > image.height) {
            val scaleHeight = width.toFloat() / image.width.toFloat()

            val newHeight = (image.height * scaleHeight).toInt()

            currentImage?.scale(width, newHeight, false)
        } else {
            val scaleWidth = height.toFloat() / image.height.toFloat()

            val newWidth = (image.width * scaleWidth).toInt()

            currentImage?.scale(newWidth, height, false)
        }

        originalImage = currentImage?.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun drawPathes() {
        listActions.forEach { action ->
            destCanvas.drawPath(action.first, action.second)
        }
    }

    private fun createPaint() = Paint().apply {
        this.isAntiAlias = false
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.ROUND
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = selectedSize
        this.color = selectedColor
        this.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    }

    private fun decodeUri(uri: Uri, requiredSize: Int): Bitmap {
        val o = BitmapFactory.Options()
        o.inJustDecodeBounds = true
        BitmapFactory.decodeStream(this.context.contentResolver.openInputStream(uri), null, o)
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
            this.context.contentResolver.openInputStream(uri),
            null,
            o2
        )!!
    }

    inner class Action(
        var first: Path,
        var second: Paint,
        var third: Boolean
    ) : Serializable
}
