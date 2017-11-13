package com.pixel.asi;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

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
     * 执行早上上班打卡操作
     * <p>
     * 只需要打开应用即可,具体的签到操作在辅助服务SIAncillaryService中执行.
     */
    public static void doMorning(Context context) {
        AppUtil.doStartApplicationWithPackageName(context, DD_PN);  // 直接打开就可以签到
    }

    /**
     * 执行晚上下班打卡操作
     * <p>
     * 只需要打开应用即可,具体的签到操作在辅助服务SIAncillaryService中执行.
     */
    public static void doEvening(Context context) {
        AppUtil.doStartApplicationWithPackageName(context, DD_PN);  // 直接打开就可以签到
    }

    /**
     * 打上班卡
     */
    public static void signMorning(AccessibilityEvent event) {

    }

    /**
     * 打下班卡
     */
    public static void signEvening(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {

        } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {

        } else if (className.equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {

        }
    }

}
