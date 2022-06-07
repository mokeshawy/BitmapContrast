package com.example.bitmapcontrast

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.bitmapcontrast.databinding.ActivityMain2Binding
import kotlin.math.pow


class MainActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding
    lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_solarus_logo)
        Glide.with(this).load(handleBitmapCorp(bitmap, 40)).centerInside().into(binding.bitmapIv)
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
        // color information
        var alpha: Int
        var read: Int
        var green: Int
        var blue: Int
        var pixel: Int
        // get contrast value
        val contrast = ((100.0 + contrastValue) / 100.0).pow(2.0)
        // scan through all pixels
        for (x in 0 until bitmapSrc.width) {
            for (y in 0 until bitmapSrc.height) {
                // get pixel color
                pixel = bitmapSrc.getPixel(x, y)
                alpha = Color.alpha(pixel)
                // apply filter contrast for every channel R, G, B
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
                // set new pixel color to output bitmap
                bitmapSrc.setPixel(x, y, Color.argb(alpha, read, green, blue))
            }
        }
    }

}