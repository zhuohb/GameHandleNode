package com.zhuohb.gamehandlenode;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class DragService extends AccessibilityService {
    @Override
    public void onServiceConnected() {
        // 服务连接成功
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理拖拽事件
    }

    @Override
    public void onInterrupt() {
        // 服务中断
    }

    public void drag(int startX, int startY, int endX, int endY) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            performDrag(nodeInfo, startX, startY, endX, endY);
        }
    }

    private void performDrag(AccessibilityNodeInfo nodeInfo, int startX, int startY, int endX, int endY) {
        // 模拟拖拽事件
    }
}