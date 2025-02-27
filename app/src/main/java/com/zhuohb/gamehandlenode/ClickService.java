package com.zhuohb.gamehandlenode;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class ClickService extends AccessibilityService {
    @Override
    public void onServiceConnected() {
        // 服务连接成功
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 处理点击事件
    }

    @Override
    public void onInterrupt() {
        // 服务中断
    }

    public void click(int x, int y) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            performClick(nodeInfo, x, y);
        }
    }

    private void performClick(AccessibilityNodeInfo nodeInfo, int x, int y) {
        // 模拟点击事件
    }
}