package ru.sad.utils.imageseg

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import com.google.mlkit.vision.segmentation.Segmenter
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions

interface SegImage {
    val segmenterOptions: SelfieSegmenterOptions

    val segmenterClient: Segmenter

    fun startSegmenter(
        image: Bitmap,
        matrix: Matrix,
        onSuccessListener: (Bitmap) -> Unit,
        onFailureListener: (String) -> Unit
    )

    fun algoSmoothEdges(bitmap: Bitmap): Bitmap
}