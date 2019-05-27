package cn.com.venvy.common.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.Toast;

import java.util.List;

import cn.com.venvy.common.interf.Method;

/**
 * Created by yanjiangbo on 2017/5/2.
 */

public class VenvyUIUtil {

    private static final String TAG = "VenvyUIUtil";
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /** 获取状态栏高度
     * @param v
     * @return
     */
    public static int getStatusBarHeight(View v) {
        if (v == null) {
            return 0;
        }
        Rect frame = new Rect();
        v.getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * dip 转换成 px
     * @param dip
     * @param context
     * @return
     */
    public static float dip2Dimension(float dip, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    /**
     * Interface definition for a callback to be invoked when the status bar changes visibility.
     * This reports <strong>global</strong> changes to the system UI state, not what the application
     * is requesting.
     *
     * @see View#setOnSystemUiVisibilityChangeListener(android.view.View.OnSystemUiVisibilityChangeListener)
     */
    public interface OnSystemUiVisibilityChangeListenerCompact {
        /**
         * Called when the status bar changes visibility because of a call to
         */
        void onSystemUiVisibilityChange(final int visibility);
    }

    /**
     * A proxy class to delegate {@linkplain OnSystemUiVisibilityChangeListenerCompact} to
     * {@linkplain OnSystemUiVisibilityChangeListener}
     *
     * @author chzhong
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static class OnSystemUiVisibilityChangeListenerProxy implements
            View.OnSystemUiVisibilityChangeListener {

        private OnSystemUiVisibilityChangeListenerCompact mListener;

        OnSystemUiVisibilityChangeListenerProxy(OnSystemUiVisibilityChangeListenerCompact listener) {
            this.mListener = listener;
        }

        public void onSystemUiVisibilityChange(int visibility) {
            // Proxy call to OnSystemUiVisibilityChangeListenerCompact
            this.mListener.onSystemUiVisibilityChange(visibility);
        }
    }

    private static class Result {
        Object obj;
        boolean complete;
    }

    private static final class SyncRunnable implements Runnable {
        private final Runnable mTarget;
        private boolean mComplete;

        public SyncRunnable(Runnable target) {

            mTarget = target;
        }

        @Override
        public void run() {

            mTarget.run();
            synchronized (this) {
                mComplete = true;
                notifyAll();
            }
        }

        public void waitForComplete() {

            synchronized (this) {
                while (!mComplete) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    /**
     * Retrieve the sHandler.
     *
     * @return the sHandler
     */
    public static Handler getHandler() {

        return sHandler;
    }

    public static Thread getUIThread() {

        return Looper.getMainLooper().getThread();
    }

    public static boolean isOnUIThread() {

        return Thread.currentThread() == getUIThread();
    }

    public static void runOnUIThread(Runnable action) {

        if (!isOnUIThread()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static boolean isAfterJellyBeanMR2() {

        return Build.VERSION.SDK_INT > 18;// Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    // All WebView method must be called in UI Thread After SDK18
    public static void runOnUIThreadAfterSDK18(Runnable action) {

        if (!isOnUIThread() && isAfterJellyBeanMR2()) {
            getHandler().post(action);
        } else {
            action.run();
        }
    }

    public static <T> T runOnUIThreadAfterSDK18(final Method<T> method) {

        if (isAfterJellyBeanMR2()) {
            return runOnUIThread(method);
        } else {
            return method.call();
        }
    }

    public static void runOnUIThreadDelay(Runnable action, long delayMillis) {

        getHandler().postDelayed(action, delayMillis);
    }

    /**
     * Execute a call on the application's main thread, blocking until it is complete. Useful for
     * doing things that are not thread-safe, such as looking at or modifying the view hierarchy.
     *
     * @param
     */
    public static void runOnUIThreadSync(Runnable action) {

        if (!isOnUIThread()) {
            SyncRunnable sr = new SyncRunnable(action);
            getHandler().post(sr);
            sr.waitForComplete();
        } else {
            action.run();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T runOnUIThread(final Method<T> method) {

        if (!isOnUIThread()) {
            final Result result = new Result();
            getHandler().post(new Runnable() {
                @Override
                public void run() {

                    synchronized (result) {
                        try {
                            result.obj = method.call();
                        } catch (Exception e) {
                            VenvyLog.w(null, e);
                        }
                        result.complete = true;
                        result.notifyAll();
                    }
                }
            });
            synchronized (result) {
                while (!result.complete) {
                    try {
                        result.wait();
                    } catch (InterruptedException e) {
                    }
                }
                return (T) result.obj;
            }
        } else {
            return method.call();
        }
    }

    public static boolean showDialogSafe(Dialog dialog) {

        try {
            dialog.show();
            return true;
        } catch (Exception e) {
            // CxLog detail informations
            VenvyLog.w(null, e);
            return false;
        }
    }

    public static boolean dismissDialogSafe(DialogInterface dialog) {

        if (dialog == null) {
            return false;
        }

        try {
            dialog.dismiss();
            return true;
        } catch (BadTokenException e) {
            VenvyLog.w(null, e.getMessage());
            return false;
        } catch (IllegalStateException e) {
            VenvyLog.w(null, e.getMessage());
            return false;
        } catch (Exception e) {
            VenvyLog.w(null, e.getMessage());
            return false;
        }
    }

    public static void showToastSafe(Context context, int msgId) {

        try {
            Toast.makeText(context, context.getString(msgId), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            VenvyLog.e(e.getMessage());
        }

    }

    public static void showToastSafe(Context context, String msg) {

        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            VenvyLog.e(e.getMessage());
        }

    }

    public static boolean showToastSafe(Toast toast) {

        try {
            toast.show();
            return true;
        } catch (Exception e) {
            // CxLog detail informations
            VenvyLog.e(e.getMessage());
            return false;
        }
    }

    public static void addView(View view, ViewGroup.LayoutParams params, WindowManager manager) {

        try {
            manager.addView(view, params);
        } catch (Exception e) {
            VenvyLog.e(TAG, e);
        }
    }

    public static boolean addViewToWindow(Context context, View view, ViewGroup.LayoutParams params) {

        if (null == view || null == context || !removeViewFromParent(view)) {
            return false;
        }

        final WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        addView(view, params, manager);
        return true;
    }

    public static boolean removeViewFromParent(View view) {

        if (null == view) {
            return false;
        }
        final ViewParent parent = view.getParent();
        if(parent == null) {
            return false;
        }
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        } else {
            return false;
        }
        return true;
    }

    public static void removeView(Context context, View view) {

        final WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        if (null == manager) {
            return;
        }
        removeView(view, manager);
    }

    public static void removeView(View view, WindowManager manager) {

        try {
            manager.removeView(view);
        } catch (Exception e) {
            VenvyLog.e(TAG, e);
        }
    }

    /*
     * Get child of the parent view, which is same type with given ViewClass.
     */
    public static View getChildByType(ViewGroup parent, Class<? extends View> viewClass) {

        if (null == parent) {
            return null;
        }
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View child = parent.getChildAt(i);
            if (child.getClass() == viewClass) {
                return child;
            }
        }

        return null;
    }

    /**
     * Check whether any provided view is visible.
     *
     * @param views the views to check.
     * @return true if there is at least one view is visible, false otherwise.
     */
    public static boolean isAnyViewVisible(View... views) {

        if (null == views || 0 == views.length) {
            return false;
        }
        for (View view : views) {
            if (view != null && view.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Count how many specified views are visible.
     *
     * @param views the views to check.
     * @return the number of view that is {@linkplain View#VISIBLE}.
     */
    public static int getVisbleViewCount(View... views) {

        if (null == views || 0 == views.length) {
            return 0;
        }
        int count = 0;
        for (View view : views) {
            if (view != null && view.getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count the total width of specified views.
     *
     * @param views the views to count total width.
     * @return the total width of specified views.
     */
    public static int getTotalViewWidth(View... views) {

        if (null == views || 0 == views.length) {
            return 0;
        }
        int width = 0;
        for (View view : views) {
            if (view != null && view.getVisibility() != View.GONE) {
                // Remember that invisible views also take space!
                width += view.getWidth();
            }
        }
        return width;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setBackground(View view, Drawable drawable) {

        if (null == view) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    /**
     * get Margin of view(left,top,right,bottom)
     *
     * @param view
     * @param gravity Gravity.START, Gravity.TOP, Gravity.END, Gravity.BOTTOM
     * @return
     */
    public static int getViewMagtin(View view, int gravity) {

        int margin = 0;
        if (null != view) {
            MarginLayoutParams params = getViewMarginLayoutParams(view);
            if (null != params) {
                switch (gravity) {
                    case Gravity.START:
                        margin = params.leftMargin;
                        break;
                    case Gravity.TOP:
                        margin = params.topMargin;
                        break;
                    case Gravity.END:
                        margin = params.rightMargin;
                        break;
                    case Gravity.BOTTOM:
                        margin = params.bottomMargin;
                        break;
                    default:
                        break;
                }
            }
        }
        return margin;
    }

    /**
     * 获取设置setText过后的TextWidth的宽度
     *
     * @param textView
     * @return
     */
    public static int getViewWidth(View textView) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(w, h);
        return textView.getMeasuredWidth();
    }

    /**
     * 获取设置setText过后的TextWidth的高度
     *
     * @param textView
     * @return
     */
    public static int getViewHight(View textView) {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        textView.measure(w, h);
        return textView.getMeasuredHeight();
    }

    /**
     * @param view
     * @return the MarginLayoutParams of a view. maybe null.
     */
    public static MarginLayoutParams getViewMarginLayoutParams(View view) {

        ViewGroup.LayoutParams params = null;
        if (null != view) {
            params = view.getLayoutParams();
        }
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) params;
            return marginLayoutParams;
        }
        return null;
    }

    /**
     * Set a listener to receive callbacks when the visibility of the system bar changes.
     *
     * @param view    the specific view to set listeners.
     * @param listner The {@link OnSystemUiVisibilityChangeListener} to receive callbacks.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setOnSystemUiVisibilityChangeListener(View view,
                                                             OnSystemUiVisibilityChangeListenerCompact listner) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        if (null == view) {
            throw new IllegalArgumentException("view may not be null.");
        }
        OnSystemUiVisibilityChangeListenerProxy listenerProxy = listner != null ? new OnSystemUiVisibilityChangeListenerProxy(
                listner)
                : null;
        view.setOnSystemUiVisibilityChangeListener(listenerProxy);
    }

    /**
     * 获取安卓状态栏栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /***
     * 判断状态栏是否显示
     * @param context
     * @return
     */
    public static boolean isShowStatus(Context context) {
        try {
            WindowManager.LayoutParams attrs = ((Activity) context).getWindow()
                    .getAttributes();

            if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                return false;// 状态栏不存在
            } else {
                return true;// 状态栏存在
            }
        } catch (Exception e) {

        }
        return false;
    }


    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isScreenOriatationPortrait(@Nullable Context context) {
        return context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(@Nullable Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(@Nullable Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /***
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(@Nullable Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getHeight();
    }


    public static int getScreenHeightByDisplayMetrics(@Nullable Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getScreenWidth(@Nullable Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获取竖屏屏幕宽度
     * @param context
     * @return
     */
    public static int getVerticalScreenWidth(@NonNull Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            return display.getWidth();
        } else {
            return display.getHeight();
        }
    }

    /**
     * 获取横屏屏幕宽度
     * @param context
     * @return
     */
    public static int getVerticalScreenHeight(@NonNull Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            return display.getHeight();
        } else {
            return display.getWidth();
        }
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int resourceId;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }

    /**
     * @param context
     * @return 是否存在导航栏
     */
    public static boolean isNavigationBarShow(Context context){
        if (!(context instanceof Activity)) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = ((Activity)(context)).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.x!=size.x;
        }else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if(menu || back) {
                return false;
            }else {
                return true;
            }
        }
    }

    /**
     * 判断 app 是否在前台
     *
     * @param context
     * @return true 说明没有在后台运行：false 在后台运行
     */
    public static boolean isTopActivity(Context context) {
        if (!(context instanceof Activity)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list == null || list.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo process : list) {
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    process.processName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取 dip
     * @param context context
     * @return DensityDpi
     */
    public static float getDensity(Context context) {
        if (context instanceof Activity) {
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay()
                    .getMetrics(dm);
            return dm.density;
        }
        return 0;
    }
}
