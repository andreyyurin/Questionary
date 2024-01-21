package ru.sad.utils.imageseg

import android.R.color
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import androidx.annotation.ColorInt
import androidx.core.graphics.toColor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.sad.utils.Four
import java.nio.ByteBuffer


class SegImageImpl : SegImage {

    private val borders = ArrayList<ArrayList<String>>()

    override val segmenterOptions: SelfieSegmenterOptions
        get() = SelfieSegmenterOptions.Builder()
            .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
            .enableRawSizeMask()
            .build()

    override val segmenterClient: Segmenter
        get() = Segmentation.getClient(segmenterOptions)

    override fun startSegmenter(
        image: Bitmap,
        matrix: Matrix,
        onSuccessListener: (Bitmap) -> Unit,
        onFailureListener: (String) -> Unit
    ) {
        val imageDetector = InputImage.fromBitmap(image, 0)

        segmenterClient
            .process(imageDetector)
            .addOnSuccessListener {
                val bitmapTemplate = Bitmap.createBitmap(
                    image.width,
                    image.height,
                    Bitmap.Config.ARGB_8888
                )

                val canvasTemplate = Canvas(bitmapTemplate)

                val bitmapResult = Bitmap.createBitmap(
                    maskColorsFromByteBuffer(it.buffer, it.width, it.height),
                    it.width,
                    it.height,
                    Bitmap.Config.ARGB_8888
                )

                val scaleX = image.width * 1f / it.width
                val scaleY = image.height * 1f / it.height

                matrix.preScale(scaleX, scaleY)

                canvasTemplate.drawBitmap(
                    image,
                    0f,
                    0f,
                    null
                )

                canvasTemplate.drawBitmap(
                    bitmapResult,
                    matrix,
                    null
                )

                CoroutineScope(Dispatchers.IO).launch {
                    for (x in 0 until bitmapTemplate.width) {
                        for (y in 0 until bitmapTemplate.height) {
                            if (bitmapTemplate.getPixel(x, y) == Color.WHITE)
                                bitmapTemplate.setPixel(x, y, Color.TRANSPARENT)
                        }
                    }

                    val result = algoSmoothEdges(bitmapTemplate)

                    onSuccessListener.invoke(result)
                    borders.clear()
                }

                bitmapResult.recycle()

                it.buffer.rewind()
            }
            .addOnFailureListener {
                onFailureListener.invoke(it.stackTraceToString())
            }
    }

    @ColorInt
    private fun maskColorsFromByteBuffer(
        byteBuffer: ByteBuffer,
        maskWidth: Int,
        maskHeight: Int
    ): IntArray {
        @ColorInt val colors = IntArray(maskWidth * maskHeight)

        for (i in 0 until maskWidth * maskHeight) {
            val backgroundLikelihood = 1 - byteBuffer.float
            val whiteColor = Color.argb(255, 255, 255, 255)
            if (backgroundLikelihood > 0.9 || backgroundLikelihood > 0.2) {
                colors[i] = whiteColor
            }
        }
        return colors
    }

    override fun algoSmoothEdges(bitmap: Bitmap): Bitmap {
        val matrixBitmap = ArrayList<ArrayList<Int>>()
        val height = bitmap.height
        val width = bitmap.width

        matrixBitmap.fillEmpty(width, height)
        matrixBitmap.fillColors(bitmap)

        for (i in 0 until width) {
            for (j in 0 until height) {
                val result = matrixBitmap.colorSum(i, j)

                val resultColor = Color.argb(
                    result.second.first,
                    result.second.second,
                    result.second.third,
                    result.second.fourth
                )
                matrixBitmap[i][j] = resultColor
            }
        }

        for (i in 0 until width) {
            for (j in 0 until height) {
                bitmap.setPixel(i, j, matrixBitmap[i][j])
            }
        }


        return bitmap
    }

    private fun ArrayList<ArrayList<Int>>.colorSum(
        i: Int,
        j: Int
    ): Pair<Int, Four<Float, Float, Float, Float>> {

        val arrayColors = arrayListOf<Int>()

        var count = 0
        for (x in i - 1 until i + 2) {
            for (y in j - 1 until j + 2) {
                count++
                if (x >= 0 && x < this.size) {
                    if (y >= 0 && y < this[x].size) {
                        arrayColors.add(this[x][y])
                    }
                }
            }
        }

        var sumr = 0f
        var sumg = 0f
        var sumb = 0f
        var suma = 0f

        val col = arrayColors.sum()

        suma += col.second.first
        sumr += col.second.second
        sumg += col.second.third
        sumb += col.second.fourth

        return if (col.first == 0)
            Pair(0, Four(0f, 0f, 0f, 0f))
        else
            Pair(
                col.first,
                Four(suma / col.first, sumr / col.first, sumg / col.first, sumb / col.first)
            )
    }

    private fun ArrayList<Int>.sum(): Pair<Int, Four<Float, Float, Float, Float>> {
        val trip = Four(0f, 0f, 0f, 0f)
        var counter = 0

        this.forEach {
            trip.first += it.toColor().alpha()
            trip.second += it.toColor().red()
            trip.third += it.toColor().green()
            trip.fourth += it.toColor().blue()
            counter++

        }
        return Pair(counter, trip)
    }

    private fun ArrayList<ArrayList<Int>>.fillEmpty(width: Int, height: Int) {
        for (i in 0 until width) {
            val list = ArrayList<Int>()

            for (j in 0 until height)
                list.add(0)

            this.add(list)
        }
    }

    private fun ArrayList<ArrayList<Int>>.fillColors(bitmap: Bitmap) {
        for (i in 0 until this.size) {
            val list = this[i]

            for (j in 0 until list.size) {
                when (bitmap.getPixel(i, j)) {
                    Color.TRANSPARENT -> {
                        this[i][j] = Color.TRANSPARENT
                    }
                    else -> {
                        this[i][j] = bitmap.getPixel(i, j)
                    }
                }
            }
        }
    }
}