package com.example.bitmapcontrast

import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Camera
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.bitmapcontrast.databinding.ActivityMain2Binding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow


class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    lateinit var bitmap: Bitmap

    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)


//        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_toyota)
//        Glide.with(this).load(handleBitmapCorp(bitmap, 20)).centerInside().into(binding.bitmapIv)

        setOnTakePhotoClicked()
        checkPermission()
        outputDirectory = getOutPutDirectory()


    }

    private fun setOnTakePhotoClicked() {
        binding.takePhotoBtn.setOnClickListener {
            takePhoto()
        }
    }

    private fun checkPermission() {
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUEST_PERMISSION, REQUEST_CODE_PERMISSION
            )
        }
    }

    private fun getOutPutDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            preview = Preview.Builder().build()
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            } catch (e: Exception) {

            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )
        val outputOption = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val saveImage = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $saveImage"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    bitmap = BitmapFactory.decodeFile(photoFile.toString())
                    binding.bitmapIv.setImageBitmap(handleBitmapCorp(bitmap, 100))
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG, "Photo capture failed: ${exception.message}", exception)
                }

            }
        )
    }

    private fun allPermissionGranted() = REQUEST_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 9) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permission not granted by the user", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun handleBitmapCorp(bitmap: Bitmap, contrastValue: Int): Bitmap {
        val colorMatrix = ColorMatrix()
        var paint = Paint()
        val croppedBitmap =
            Bitmap.createBitmap(bitmap, 200, 300, bitmap.width - 500, bitmap.height - 620)
        colorMatrix.setSaturation(10.2f)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        Canvas(croppedBitmap).drawBitmap(croppedBitmap, 0f, 0f, paint)
        setBitmapContrast(croppedBitmap, contrastValue)
        paint = Paint()
        colorMatrix.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        Canvas(croppedBitmap).drawBitmap(croppedBitmap, 0f, 0f, paint)
        return croppedBitmap
    }


    private fun setBitmapContrast(bitmapSrc: Bitmap, contrastValue: Int) {
        var alpha: Int
        var read: Int
        var green: Int
        var blue: Int
        var pixel: Int
        val contrast = ((100.0 + contrastValue) / 100.0).pow(2.0)
        for (x in 0 until bitmapSrc.width) {
            for (y in 0 until bitmapSrc.height) {
                pixel = bitmapSrc.getPixel(x, y)
                alpha = Color.alpha(pixel)
                read = Color.red(pixel)
                read = (((read / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (read < 0) {
                    read = 0
                } else if (read > 255) {
                    read = 255
                }
                green = Color.green(pixel)
                green = (((green / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (green < 0) {
                    green = 0
                } else if (green > 255) {
                    green = 255
                }
                blue = Color.blue(pixel)
                blue = (((blue / 255.0 - 0.5) * contrast + 0.5) * 255.0).toInt()
                if (blue < 0) {
                    blue = 0
                } else if (blue > 255) {
                    blue = 255
                }
                bitmapSrc.setPixel(x, y, Color.argb(alpha, read, green, blue))
            }
        }
    }

    companion object {
        private const val TAG = "CameraXExample"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSION = 10
        private val REQUEST_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)

    }
}