package com.pixel.asi;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * 辅助服务
 * <p>
 * http://www.jianshu.com/p/4cd8c109cdfb
 */
public class SIAncillaryService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        String eventTypeName = "";
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:  // 类型视图中点击
                eventTypeName = "类型视图中点击";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:  // 类型的观点集中
                eventTypeName = "类型的观点集中";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED: // 类型视图长点击
                eventTypeName = "类型视图长点击";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED: // 所选视图类型
                eventTypeName = "所选视图类型";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: // 文本类型观点有所改变
                eventTypeName = "文本类型观点有所改变";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:  // 类型窗口状态改变
                eventTypeName = "类型窗口状态改变 主要是Activity的改变"; // TODO 每次 event.getClassName() 会拿到Activity的名称

                SignInUtil.punch(this, event);
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:    // 式通知状态改变
                eventTypeName = "式通知状态改变";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END: // 打字触控手势结束
                eventTypeName = "打字触控手势结束";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:  // 类型声明
                eventTypeName = "类型声明";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:   // 启动触控探测手势
                eventTypeName = "启动触控探测手势";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:  // 类型查看鼠标输入
                eventTypeName = "类型查看鼠标输入";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:   // 类型视图盘旋退出
                eventTypeName = "类型视图盘旋退出";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED: // 界面在滚动
                eventTypeName = "界面在滚动";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:   // 文本发生了改变
                eventTypeName = "文本发生了改变";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:    // 窗口内容发生了改变
                eventTypeName = "窗口内容发生了改变 主要是View控件的改变";    // TODO 每次event.getClassName() 会拿到View的名称
                break;
            default:
                eventTypeName = "没有匹配事件";
        }
        Log.e("SIAncillaryService", "eventType:" + eventType + "\t" + "eventTypeName:" + eventTypeName + "\nClassName:" + event.getClassName());
    }

    @Override
    public void onInterrupt() {
        Log.e("SIAncillaryService", "当前辅助服务被系统中断");
    }

}
