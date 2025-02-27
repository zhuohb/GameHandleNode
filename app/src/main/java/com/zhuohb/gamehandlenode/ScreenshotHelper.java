package com.zhuohb.gamehandlenode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.nio.ByteBuffer;

public class ScreenshotHelper {
    private Context context;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;
    private ScreenshotCallback callback;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface ScreenshotCallback {
        void onScreenshotTaken(Bitmap bitmap);
    }

    public ScreenshotHelper(Context context) {
        this.context = context;
    }

    public void setScreenshotCallback(ScreenshotCallback callback) {
        this.callback = callback;
    }

    public void takeScreenshot(MediaProjection mediaProjection) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        int density = metrics.densityDpi;

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenCapture",
                width, height, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(), null, null);

        imageReader.setOnImageAvailableListener(reader -> {
            Image image = null;
            try {
                image = reader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * width;

                    Bitmap bitmap = Bitmap.createBitmap(
                            width + rowPadding / pixelStride,
                            height,
                            Bitmap.Config.ARGB_8888
                    );
                    bitmap.copyPixelsFromBuffer(buffer);

                    // 裁剪到正确的宽度
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    bitmap.recycle();

                    if (callback != null) {
                        handler.post(() -> callback.onScreenshotTaken(croppedBitmap));
                    }
                }
            } finally {
                if (image != null) {
                    image.close();
                }
            }

            // 清理资源
            virtualDisplay.release();
            imageReader.close();
            mediaProjection.stop();
        }, handler);
    }
}