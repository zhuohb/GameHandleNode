package com.zhuohb.gamehandlenode;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    private WebSocket webSocket;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 初始化 WebSocket
        initWebSocket();
    }

    private void initWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://your-websocket-server-url")
                .build();

        WebSocketListener webSocketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                // WebSocket 连接成功
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
                // 连接失败
            }
        };
        webSocket = client.newWebSocket(request, webSocketListener);
    }

    private void handleMessage(String message) {
        // 处理接收到的消息
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }
}