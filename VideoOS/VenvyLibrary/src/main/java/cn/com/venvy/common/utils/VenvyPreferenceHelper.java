package cn.com.venvy.common.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Arthur on 2017/4/27.
 */

public class VenvyPreferenceHelper {

    /**
     * 查询某个key是否已经存在
     *
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, @NonNull String name, @NonNull String key) {
        SharedPreferences sp = context.getSharedPreferences(name,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }


    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.apply();
        }
    }

    public static void reset(final Context ctx, @NonNull String fileName) {
        SharedPreferences.Editor edit = ctx.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE).edit();
        edit.clear();
        edit.apply();
    }

    public static void put(Context mContext, @NonNull String fileName, @NonNull String key, String value) {
        putString(mContext, fileName, key, value);
    }

    public static void put(Context mContext, @NonNull String fileName, @NonNull String key, int value) {
        putInt(mContext, fileName, key, value);
    }

    public static void put(Context mContext, @NonNull String fileName, @NonNull String key, float value) {
        putFloat(mContext, fileName, key, value);
    }

    public static void put(Context mContext, @NonNull String fileName, @NonNull String key, boolean value) {
        putBoolean(mContext, fileName, key, value);
    }

    public static SharedPreferences getPreferences(Context mContext, @NonNull String fileName) {
        return mContext.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE);
    }


    public static boolean hasString(Context mContext, @NonNull String fileName, @NonNull String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    public static void remove(Context context, @NonNull String fileName, String... keys) {
        if (keys != null) {
            SharedPreferences sharedPreferences = context
                    .getSharedPreferences(fileName, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (String key : keys) {
                editor.remove(key);
            }
            editor.apply();
        }
    }

    public static void clearData(Context context, @NonNull String fileName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void putString(Context context, @NonNull String fileName, @NonNull String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, @NonNull String fileName, @NonNull String key, String defValue) {
        return context.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE).getString(key, defValue);
    }

    public static int getInt(Context mContext, @NonNull String name, @NonNull String key) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                name, Activity.MODE_PRIVATE);
        return mSharedPreferences.getInt(key, 0);
    }

    public static void putInt(Context mContext, @NonNull String fileName, @NonNull String key, int value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putBoolean(Context mContext, @NonNull String name, @NonNull String key,
                                  boolean value) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                name, Activity.MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context mContext, @NonNull String name, @NonNull String key) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                name, Activity.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(key, false);
    }

    public static void putLong(Context mContext, @NonNull String name, @NonNull String key,
                               long value) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                name, Activity.MODE_PRIVATE);
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public static long getLong(Context mContext, @NonNull String name, @NonNull String key) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
                name, Activity.MODE_PRIVATE);
        return mSharedPreferences.getLong(key, 0);
    }

    public static void putFloat(Context mContext, @NonNull String fileName, @NonNull String key, float value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                fileName, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context mContext, @NonNull String fileName, @NonNull String key, float defValue) {
        return mContext.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE).getFloat(key, defValue);
    }
}
