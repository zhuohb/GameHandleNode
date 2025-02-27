package com.zhuohb.gamehandlenode;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    private ScreenshotHelper screenshotHelper;
    private WebSocket webSocket;
    private OkHttpClient client;
    private EditText websocketUrlInput;
    private Button connectButton;

    private static final int REQUEST_MEDIA_PROJECTION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // 初始化 UI 组件
        websocketUrlInput = findViewById(R.id.websocketUrlInput);
        connectButton = findViewById(R.id.connectButton);
        // 设置点击监听器
        connectButton.setOnClickListener(v -> {
            String url = websocketUrlInput.getText().toString();
            if (!url.isEmpty()) {
                initWebSocket(url);
                connectButton.setEnabled(false);
                connectButton.setText("连接中...");
            }
        });

        screenshotHelper = new ScreenshotHelper(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initWebSocket(String url) {
        if (webSocket != null) {
            webSocket.close(1000, "New connection requested");
        }

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                runOnUiThread(() -> {
                    connectButton.setText("已连接");
                    Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                });
                webSocket.send("Connected to Android client");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 接收文本消息
                runOnUiThread(() -> handleMessage(text));
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                // 接收二进制消息
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                // WebSocket 正在关闭
                webSocket.close(1000, null);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                runOnUiThread(() -> {
                    connectButton.setEnabled(true);
                    connectButton.setText("连接");
                    Toast.makeText(MainActivity.this, "连接失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        };
        webSocket = client.newWebSocket(request, webSocketListener);
    }

    private void handleMessage(String message) {
        if ("screen".equals(message)) {
            // 请求屏幕截图权限
            MediaProjectionManager projectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(
                    projectionManager.createScreenCaptureIntent(),
                    REQUEST_MEDIA_PROJECTION
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION && resultCode == RESULT_OK) {
            MediaProjectionManager projectionManager = (MediaProjectionManager)
                    getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            MediaProjection mediaProjection = projectionManager.getMediaProjection(resultCode, data);

            // 设置截图回调
            screenshotHelper.setScreenshotCallback(bitmap -> {
                if (bitmap != null && webSocket != null) {
                    // 将 Bitmap 转换为 Base64 字符串
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                    String base64Image = Base64.encodeToString(
                            byteArrayOutputStream.toByteArray(),
                            Base64.DEFAULT
                    );

                    // 发送到 WebSocket 服务器
                    webSocket.send("data:image/jpeg;base64," + base64Image);
                    bitmap.recycle();
                }
            });

            // 执行截图
            screenshotHelper.takeScreenshot(mediaProjection);
        }
    }
}