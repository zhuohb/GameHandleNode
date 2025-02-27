package com.zhuohb.gamehandlenode;

import android.content.Context;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

public class ScreenshotHelper {
    private Context context;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;

    public ScreenshotHelper(Context context) {
        this.context = context;
        mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    public void takeScreenshot(MediaProjection mediaProjection) {

    }
}