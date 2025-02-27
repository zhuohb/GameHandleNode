package com.zhuohb.gamehandlenode;

import android.media.projection.MediaProjection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClient {
    private MediaProjection mediaProjection;
    private ScreenshotHelper screenshotHelper;
    private WebSocket webSocket;
    private OkHttpClient client = new OkHttpClient();

    public void connect(String url) {
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                // 连接成功
                super.onOpen(webSocket, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 收到消息
                processCommand(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // 连接关闭
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // 连接失败
                super.onFailure(webSocket, t, response);
            }
        });
    }

    private void processCommand(String command) {
        // 根据命令执行相应操作
        if (command.equals("screenshot")) {
            takeScreenshot();
        } else if (command.startsWith("click")) {
            // 解析点击坐标
            String[] parts = command.split(":");
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            click(x, y);
        } else if (command.startsWith("drag")) {
            // 解析拖拽坐标
            String[] parts = command.split(":");
            int startX = Integer.parseInt(parts[1]);
            int startY = Integer.parseInt(parts[2]);
            int endX = Integer.parseInt(parts[3]);
            int endY = Integer.parseInt(parts[4]);
            drag(startX, startY, endX, endY);
        }
    }
    private void takeScreenshot() {
        // TODO: 实现截图功能
        // 可以调用之前实现的 ScreenshotHelper
        if (screenshotHelper != null && mediaProjection != null) {
            screenshotHelper.takeScreenshot(mediaProjection);
        }
    }
}