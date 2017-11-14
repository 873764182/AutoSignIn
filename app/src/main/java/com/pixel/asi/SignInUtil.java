package com.pixel.asi;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 * <p>
 * 执行签到工具
 */

public class SignInUtil {
    // 钉钉的应用包名
    public static String DD_PN = "com.alibaba.android.rimet";
    public static String GOTO_TIME = "GOTO_TIME";
    public static String AFTER_TIME = "AFTER_TIME";
    public static String WIFI_SSID = "WIFI_SSID";
    private static String CHECK_MORNING = "MORNING";
    private static String CHECK_EVENING = "EVENING";

    public static String mKey() {
        return CHECK_MORNING + AppUtil.getDate();
    }

    public static String eKey() {
        return CHECK_EVENING + AppUtil.getDate();
    }

    /**
     * 计算时间的分钟值
     *
     * @param timeString 时间字符串 10:00格式
     * @return 分钟值
     */
    public static int computationsTimeDifference(String timeString) {
        try {
            String[] tArr = timeString.split(":");
            int hs = Integer.parseInt(tArr[0]) * 60;
            int ss = Integer.parseInt(tArr[1]);
            return hs + ss;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 计算时间
     *
     * @param context 上下文
     * @return 小于0或者等于0为不在打开时间范围 1.上班打开时间区间 2.下班打卡时间区间
     */
    public static int computationsTime(Context context) {

        // 不在指定网络不签到
        String wifiName = AppUtil.getWifiName(context);
        if (wifiName == null || !wifiName.equalsIgnoreCase(ConfigUtil.getString(context, SignInUtil.WIFI_SSID))) {
            return -1;
        }

        // 星期日与星期六不执行
        int week = AppUtil.getWeek();
        if (week == 0 || week == 6) {
            return -1;
        }

        // 上班时间 9:30 之前的30分钟内执行 与 下班后 18:00 之后30分钟内执行 执行范围: 9:00-9:30, 18:00-18:30
        int hour = AppUtil.getHour();
        int minute = AppUtil.getMinute();

        // 当前时间大小
        int t1 = SignInUtil.computationsTimeDifference(hour + ":" + minute);
        // 上班时间大小
        int t2 = SignInUtil.computationsTimeDifference(ConfigUtil.getString(context, SignInUtil.GOTO_TIME));
        // 下班时间大小
        int t3 = SignInUtil.computationsTimeDifference(ConfigUtil.getString(context, SignInUtil.AFTER_TIME));

        if (t1 < t2 && (t2 - t1) < 30 && !ConfigUtil.getBoolean(context, SignInUtil.mKey())) {    // 上班时间前30分钟内

            return 1;

        } else if (t1 > t3 && (t1 - t3) < 30 && !ConfigUtil.getBoolean(context, SignInUtil.eKey())) { // 下班时间后30分钟内

            return 2;

        } else {
            return -1; // 不在签到时间内
        }
    }

    /**
     * 执行早上上班打卡操作
     * <p>
     * 只需要打开应用即可,具体的签到操作在辅助服务SIAncillaryService中执行.
     */
    public static void doMorning(Context context) {
        AppUtil.doStartApplicationWithPackageName(context, DD_PN);  // 直接打开 后面交给辅助服务接手 执行签到
    }

    /**
     * 执行晚上下班打卡操作
     * <p>
     * 只需要打开应用即可,具体的签到操作在辅助服务SIAncillaryService中执行.
     */
    public static void doEvening(Context context) {
        AppUtil.doStartApplicationWithPackageName(context, DD_PN);  // 直接打开 后面交给辅助服务接手 执行签到
    }

    /**
     * 打上班卡
     */
    @Deprecated
    public static void signMorning(AccessibilityService service, AccessibilityEvent event) {

        ConfigUtil.saveBoolean(service, SignInUtil.mKey(), true);
    }

    /**
     * 打下班卡
     */
    @Deprecated
    public static void signEvening(AccessibilityService service, AccessibilityEvent event) {
        AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
        List<AccessibilityNodeInfo> nodeInfoList =  // 在列表上查找自己的公司
                accessibilityNodeInfo.findAccessibilityNodeInfosByText("工作通知:广州智菁通信息科技有限公司".trim());
        if (nodeInfoList != null && nodeInfoList.size() > 0) {
            AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0); // textView
            try {
                // 原本的层级: TextView => LinearLayout => FrameLayout => RelativeLayout
                AccessibilityNodeInfo nodeRelativeLayout = nodeInfo.getParent();  // relativeLayout
                if (nodeRelativeLayout != null) {
                    nodeRelativeLayout.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 进入公司页面
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConfigUtil.saveBoolean(service, SignInUtil.eKey(), true);
    }

    // 执行打卡
    public static void punch(final AccessibilityService service, AccessibilityEvent event) {

        final int state = computationsTime(service);
        if (state <= 0) {
            Log.e("SignInUtil", "当前不是打卡时间");
            return;
        }

        String className = event.getClassName().toString();
        // 打卡界面
        if ("com.alibaba.lightapp.runtime.activity.CommonWebViewActivity".equalsIgnoreCase(className)) {
            AsiApplication.run(new Runnable() {
                @Override
                public void run() {
                    // TODO 执行签到操作 根据上班下班不同 点击不同的屏幕位置
                    if (state == 1 && !ConfigUtil.getBoolean(service, SignInUtil.mKey())) {   // 上班卡
                        try {
                            // AppUtil.execShellCmd("input tap 530 700");

                            ConfigUtil.saveBoolean(service, SignInUtil.mKey(), true);

                            AppUtil.doStartApplicationWithPackageName(service, "com.pixel.asi");    // 返回应用
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (state == 2 && !ConfigUtil.getBoolean(service, SignInUtil.eKey())) {    // 下班卡
                        try {
                            AppUtil.execShellCmd("input tap 530 1220"); // 下班打卡按钮位置

                            ConfigUtil.saveBoolean(service, SignInUtil.eKey(), true);

                            AppUtil.doStartApplicationWithPackageName(service, "com.pixel.asi");    // 返回应用
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 1000 * 5);   // WebView的界面加载可能比较久
        } else /*if ("com.alibaba.android.rimet.biz.SplashActivity".equalsIgnoreCase(className))*/ {    // 这是主界面 只有第一次打开时才有SplashActivity 所以取消判断界面直接判断元素是否符合
            AccessibilityNodeInfo accessibilityNodeInfo = service.getRootInActiveWindow();
            if (accessibilityNodeInfo == null) {
                return;
            }
            List<AccessibilityNodeInfo> nodeInfoList =
                    accessibilityNodeInfo.findAccessibilityNodeInfosByText("考勤打卡"); // 模糊搜索
            if (nodeInfoList == null) {
                return;
            }
            for (final AccessibilityNodeInfo nodeInfo : nodeInfoList) {
                if ("考勤打卡".equalsIgnoreCase(nodeInfo.getText().toString())) {
                    AsiApplication.run(new Runnable() {
                        @Override
                        public void run() {
                            nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK); // 找到考勤打卡按钮后点击进入打卡界面
                        }
                    }, 2000);
                    return;
                }
            }
        }
    }

}
