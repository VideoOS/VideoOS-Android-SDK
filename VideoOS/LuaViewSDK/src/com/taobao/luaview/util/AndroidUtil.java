
package com.taobao.luaview.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


public class AndroidUtil {

    /**
     * 手机型号
     */
    public static String getOsModel() {
        return Build.MODEL;
    }

    /**
     * 系统品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getProduct() {
        return Build.PRODUCT;
    }

    public static String getDevice() {
        return Build.DEVICE;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * get density of screen
     */
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * get screen width
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * get screen height
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取window的size
     */
    static int[] getWindowSize(Context context) {
         WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
         Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point point = new Point();
            display.getSize(point);
            return new int[]{point.x, point.y};
        } else {
            return new int[]{display.getWidth(), display.getHeight()};
        }
    }

    /**
     * 获取window的size
     */
    public static int[] getWindowSizeInDp(Context context) {
         int[] size = getWindowSize(context);
        return new int[]{(int) DimenUtil.pxToDpi(size[0]), (int) DimenUtil.pxToDpi(size[1])};
    }


    /**
     * get action bar height
     */
    static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        if (context instanceof Activity && ((Activity) context).getActionBar() != null) {
            actionBarHeight = ((Activity) context).getActionBar().getHeight();
        }

        if (actionBarHeight == 0) {
             TypedValue tv = new TypedValue();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && (context.getTheme() != null && context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }
        }
        return actionBarHeight;
    }

    /**
     * get actionbar height
     */
    public static int getActionBarHeightInDp(Context context) {
        return (int) DimenUtil.pxToDpi(getActionBarHeight(context));
    }


    /**
     * 获取系统的Navigation bar height
     */
    public static int getNavigationBarHeightInDp(Context context) {
        Point size = getNavigationBarSize(context);
        return (int) DimenUtil.pxToDpi(size != null ? size.y : 0);
    }

    static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (Exception e) {
            }
        }
        return size;
    }

    /**
     * get status bar height in dp
     */
    public static int getStatusBarHeightInDp(Context context) {
        return (int) DimenUtil.pxToDpi(getStatusBarHeight(context));
    }

    static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
