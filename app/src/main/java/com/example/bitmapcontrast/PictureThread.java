package com.example.bitmapcontrast;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Handler;

import androidx.appcompat.widget.AppCompatImageView;

public class PictureThread extends Thread {

    private AppCompatImageView appCompatImageView;
    private Bitmap bitmap;
    private Bitmap bit_map;
    private Canvas canvas;
    private Paint paint;
    private ColorMatrix colorMatrix;
    private ColorMatrixColorFilter colorMatrixColorFilter;
    private Handler handler;
    private boolean running = false;

    public PictureThread(AppCompatImageView appCompatImageView, Bitmap bitmap) {
        this.appCompatImageView = appCompatImageView;
        this.bitmap = bitmap;
        bit_map = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        canvas = new Canvas(bit_map);
        paint = new Paint();
        handler = new Handler();
    }

    public void adjustBrightness(int amount) {
        colorMatrix = new ColorMatrix(new float[]{
                1, 0, 0, 0, amount,
                0, 1f, 0, 0, amount,
                0, 0, 1f, 0, amount,
                0, 0, 0, 1f, 0
        });
        colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        running = true;
    }

    @Override
    public void run() {
        while (true) {
            if (running) {
                canvas.drawBitmap(bitmap, 0, 0, paint);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        appCompatImageView.setImageBitmap(bit_map);
                        running = false;
                    }
                });
            }
        }
    }
}
