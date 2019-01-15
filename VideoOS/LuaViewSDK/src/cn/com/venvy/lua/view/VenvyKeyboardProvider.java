package cn.com.venvy.lua.view;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;


public class VenvyKeyboardProvider extends PopupWindow {


    private int mKeyboardLandscapeHeight;

    private int mKeyboardPortraitHeight;

    private View mPopupView;

    private View mParentView;

    private WeakReference<Activity> mReference;

    private int currentHieght;

    public VenvyKeyboardProvider(Activity activity) {
        super(activity);
        this.mReference = new WeakReference<>(activity);

        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ll.setBackgroundColor(0x00000000);
        this.mPopupView = ll;
        setContentView(mPopupView);

        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager
                .LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        mParentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);

        mPopupView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                .OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (mPopupView != null) {
                    handleOnGlobalLayout();
                }
            }
        });
    }


    public void start() {
        if (!isShowing() && mParentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(mParentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    private int getScreenOrientation() {
        Activity activity = mReference.get();
        if (activity != null) {
            return activity.getResources().getConfiguration().orientation;
        }
        return 0;
    }

    private void handleOnGlobalLayout() {
        Activity activity = mReference.get();
        if (activity == null) {
            return;
        }
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        mPopupView.getWindowVisibleDisplayFrame(rect);

        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;

        if (orientation == Configuration.ORIENTATION_PORTRAIT && currentHieght != keyboardHeight) {
            this.mKeyboardPortraitHeight = keyboardHeight;
            notifyKeyboardHeightChanged(mKeyboardPortraitHeight, orientation);
            currentHieght = keyboardHeight;
        }
    }

    private void notifyKeyboardHeightChanged(int height, int orientation) {
        Bundle bundle = new Bundle();
        bundle.putInt("height", height);
        bundle.putInt("orientation", orientation);
        ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_KEYBOARD_STATUS_CHANGED, bundle);
    }
}