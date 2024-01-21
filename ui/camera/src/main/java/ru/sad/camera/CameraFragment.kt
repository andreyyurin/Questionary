package ru.sad.camera

import android.Manifest.permission.CAMERA
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Rational
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.copyToFile
import ru.sad.base.ext.smoothHide
import ru.sad.base.ext.smoothShow
import ru.sad.base.ext.tint
import ru.sad.camera.databinding.FragmentCameraBinding
import ru.sad.utils.runOnIoThread
import ru.sad.utils.runOnUiThread

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {

    companion object {
        private const val DATA_PHOTO = "DATA_PHOTO"
    }

    override val bindingInflater: (LayoutInflater) -> FragmentCameraBinding =
        FragmentCameraBinding::inflate

    private var imageCapture: ImageCapture? = null
    private var isFlashlightEnabled = false
    private var lens = CameraSelector.LENS_FACING_BACK

    private val viewModel: CameraViewModel by viewModels()

    private lateinit var cameraSelector: CameraSelector

    private val captureListener = object : OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            binding.viewFlashlight.smoothHide()

            binding.layoutSavePhoto.smoothShow()

            val rotate = image.imageInfo.rotationDegrees

            val bmp = decodeBitmap(
                image,
                rotate,
                lens == CameraSelector.LENS_FACING_FRONT
            )

            binding.previewImage.setImageBitmap(bmp)

            binding.ivOk.setOnClickListener {
                saveImage(bmp, it.context)
            }
        }

        override fun onError(exception: ImageCaptureException) {
            super.onError(exception)
            binding.viewFlashlight.smoothHide()
        }
    }

    override fun setup(savedInstanceState: Bundle?) {
        checkPermissions(
            CAMERA,
            onGranted = ::setupCamera
        )
    }

    private fun setupCamera() {
        startCamera()
        bindListeners()
    }

    private fun bindListeners() {
        binding.btnTakePhoto.setOnClickListener {
            if (lens == CameraSelector.LENS_FACING_FRONT && isFlashlightEnabled) binding.viewFlashlight.smoothShow()

            imageCapture?.takePicture(
                ContextCompat.getMainExecutor(it.context), captureListener
            )
        }

        binding.ivRetry.setOnClickListener {
            binding.layoutSavePhoto.smoothHide()
        }

        binding.ivFlipCamera.setOnClickListener {
            lens = if (CameraSelector.LENS_FACING_FRONT == lens)
                CameraSelector.LENS_FACING_BACK
            else
                CameraSelector.LENS_FACING_FRONT

            rotateFlipImage()
            startCamera()
        }

        binding.ivFlashlight.setOnClickListener {
            imageCapture?.flashMode = FLASH_MODE_ON
            setFlashlight(!isFlashlightEnabled)
        }
    }

    private fun setFlashlight(flash: Boolean) {
        binding.ivFlashlight.tint(if (flash) ru.sad.base.R.color.yellow else ru.sad.base.R.color.main_icon_color)
        imageCapture?.flashMode = if (flash) FLASH_MODE_ON else FLASH_MODE_OFF
        isFlashlightEnabled = flash
    }

    private fun saveImage(image: Bitmap, context: Context) {
        this@CameraFragment.runOnIoThread({
            runOnUiThread {
                showLoading()
            }

            findNavController().previousBackStackEntry?.savedStateHandle?.getLiveData<String>(
                DATA_PHOTO
            )?.postValue(
                getBitmapUri(
                    image,
                    context
                ).toString()
            )

            runOnUiThread {
                hideLoading()
                viewModel.exit()
            }
        }, {
        })
    }

    private fun getBitmapUri(image: Bitmap, context: Context): Uri? {
        val values = ContentValues().apply {
            put(
                MediaStore.Images.Media.MIME_TYPE,
                getString(ru.sad.base.R.string.main_photo_mime_type)
            )
            put(
                MediaStore.Images.Media.TITLE,
                "lifestory" + System.currentTimeMillis()
            )
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "lifestory" + System.currentTimeMillis()
            )
        }

        val uri =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val bmp = resize(image, 512, 512)

        uri?.let { uri ->
            val stream = context.contentResolver.openOutputStream(uri)

            stream?.let { os ->
                bmp?.compress(Bitmap.CompressFormat.WEBP, 100, os)
            }

            stream?.close()
        }

        return uri?.copyToFile(context)?.toUri()
    }

    private fun startCamera() {
        context?.let {
            setFlashlight(false)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(it)

            cameraProviderFuture.addListener({

                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder()
                    .build()
                    .also { preview ->
                        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder().build()

                imageCapture?.setCropAspectRatio(Rational(3, 4))

                try {
                    cameraProvider.unbindAll()

                    cameraSelector = CameraSelector.Builder().requireLensFacing(lens).build()

                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {

                }

            }, ContextCompat.getMainExecutor(it))
        }
    }

    private fun decodeBitmap(image: ImageProxy, rotate: Int, mirrorImage: Boolean): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity()).also { buffer.get(it) }

        val matrix = Matrix()

        if (mirrorImage) {
            matrix.preScale(-1f, 1f)
            matrix.postRotate(-rotate.toFloat())
        } else {
            matrix.postRotate(rotate.toFloat())
        }

        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        image.close()
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    }

    private fun rotateFlipImage() {
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 500
        rotate.interpolator = LinearInterpolator()

        binding.ivFlipCamera.startAnimation(rotate)
    }

    private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        var image = image
        return if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > ratioBitmap) {
                finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
            } else {
                finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
            image
        } else {
            image
        }
    }
}