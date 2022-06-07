package com.example.bitmapcontrast

import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bitmapcontrast.databinding.ActivityMainBinding
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var bitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val bitmapResourceId = R.drawable.ic_solarus_logo

        binding.bitmapIv.setImageBitmap(BitmapFactory.decodeResource(resources, bitmapResourceId))

        binding.cutBtn.setOnClickListener {
            bitmap = BitmapFactory.decodeResource(resources, bitmapResourceId)
            bitmap = getCircularBitmap(bitmap)
            binding.bitmapIv.setImageBitmap(bitmap)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap?): Bitmap {
        val squareBitmapWidth = min(bitmap!!.width, bitmap.height)
        val dstBitmap =
            Bitmap.createBitmap(squareBitmapWidth, squareBitmapWidth, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        val rect = Rect(0, 0,squareBitmapWidth, squareBitmapWidth)
        val rectF = RectF(rect)
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val left = ((squareBitmapWidth - bitmap.width) / 2).toFloat()
        val top = ((squareBitmapWidth - bitmap.height) / 2).toFloat()
        canvas.drawBitmap(bitmap, left, top, paint)
        bitmap.recycle()
        return dstBitmap
    }
}