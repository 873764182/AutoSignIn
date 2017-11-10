package com.pixel.asi;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by panxi on 2016/4/22.
 * <p>
 * 保存简单键值对信息(SharedPreferences)
 */
public class ConfigUtil {
    private static final String NAME = ConfigUtil.class.getName();
    private static SharedPreferences preferences = null;
    private static SharedPreferences.Editor editor = null;

    /**
     * 获取配置对象
     */
    public static SharedPreferences getPreferences(@NonNull Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        }
        return preferences;
    }

    /**
     * 获取配置编辑对象
     */
    public static SharedPreferences.Editor getEditor(@NonNull Context context) {
        if (editor == null) {
            editor = getPreferences(context).edit();
        }
        return editor;
    }

    /**
     * 保存配置
     */
    public static boolean saveString(@NonNull Context context, @NonNull String key, @NonNull String value) {
        return getEditor(context).putString(key, value).commit();
    }

    /**
     * 保存配置
     */
    public static boolean saveInt(@NonNull Context context, @NonNull String key, @NonNull int value) {
        return getEditor(context).putInt(key, value).commit();
    }

    /**
     * 保存配置
     */
    public static boolean saveBoolean(@NonNull Context context, @NonNull String key, @NonNull boolean value) {
        return getEditor(context).putBoolean(key, value).commit();
    }

    /**
     * 保存配置
     */
    public static boolean saveLong(@NonNull Context context, @NonNull String key, @NonNull long value) {
        return getEditor(context).putLong(key, value).commit();
    }

    /**
     * 保存配置
     */
    public static boolean saveFloat(@NonNull Context context, @NonNull String key, @NonNull float value) {
        return getEditor(context).putFloat(key, value).commit();
    }

    /**
     * 读取配置
     */
    public static String getString(@NonNull Context context, @NonNull String key) {
        return getPreferences(context).getString(key, null);
    }

    /**
     * 读取配置
     */
    public static int getInt(@NonNull Context context, @NonNull String key) {
        return getPreferences(context).getInt(key, -1);
    }

    /**
     * 读取配置
     */
    public static boolean getBoolean(@NonNull Context context, @NonNull String key) {
        return getPreferences(context).getBoolean(key, false);
    }

    /**
     * 读取配置
     */
    public static long getLong(@NonNull Context context, @NonNull String key) {
        return getPreferences(context).getLong(key, -1L);
    }

    /**
     * 读取配置
     */
    public static float getFloat(@NonNull Context context, @NonNull String key) {
        return getPreferences(context).getFloat(key, -1F);
    }
}
