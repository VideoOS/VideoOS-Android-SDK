/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.userdata.ui.UDViewGroup;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.foreground.ForegroundRelativeLayout;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

import cn.com.venvy.common.utils.VenvyLog;

/**
 * LuaView-ViewGroup
 * 容器类
 *
 * @author song
 * @date 15/8/20
 */
public class LVViewGroup<T extends UDViewGroup> extends ForegroundRelativeLayout implements ILVViewGroup {
    protected T mLuaUserdata;

    /**
     * Flexbox attributes
     */
    private ArrayList<UDView> mChildNodeViews;

    public LVViewGroup(Globals globals, LuaValue metaTable, Varargs varargs) {
        this(globals != null ? globals.getContext() : null, globals, metaTable, varargs);
    }

    public LVViewGroup(Context context, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(context);
        this.mLuaUserdata = createUserdata(globals, metaTable, varargs);
    }


    /**
     * create user data
     *
     * @param globals
     * @param metaTable
     * @param varargs
     * @return
     */
    public T createUserdata(Globals globals, LuaValue metaTable, Varargs varargs) {
        return (T) (new UDViewGroup(this, globals, metaTable, varargs));
    }

    @Override
    public T getUserdata() {
        return mLuaUserdata;
    }

    public void show() {
        LVViewGroup.this.setVisibility(View.VISIBLE);
    }


    //-------------------------------------------显示回调--------------------------------------------


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerHomeKeyReceiver(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        unregisterHomeKeyReceiver(getContext());
        super.onDetachedFromWindow();

    }

    private HomeKeyBroadcastReceiver homeKeyBroadcastReceiver;

    private void registerHomeKeyReceiver(Context context) {

        try {
            if (homeKeyBroadcastReceiver == null) {
                homeKeyBroadcastReceiver = new HomeKeyBroadcastReceiver();
                final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                context.registerReceiver(homeKeyBroadcastReceiver, homeFilter);
            }
        } catch (Exception e) {
            VenvyLog.e("VideoOS", e);
        }
    }

    private void unregisterHomeKeyReceiver(Context context) {

        try {
            if (null != homeKeyBroadcastReceiver) {
                context.unregisterReceiver(homeKeyBroadcastReceiver);
            }
            homeKeyBroadcastReceiver = null;
        } catch (Exception e) {
            VenvyLog.e("VideoOS", e);
        }
    }

    public class HomeKeyBroadcastReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    if (mLuaUserdata != null) {
                        mLuaUserdata.callOnHome();
                    }

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    VenvyLog.i(LOG_TAG, "long press home key or activity switch");
                    if (mLuaUserdata != null) {
                        mLuaUserdata.callOnHome();
                    }

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏

                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    if (mLuaUserdata != null) {
                        mLuaUserdata.callOnHome();
                    }
                }
            }
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == View.VISIBLE) {
            mLuaUserdata.callOnShow();
        } else {//这里会有 INVISIBLE 和 GONE 两种状态，INVISIBLE 也会调用，从后台进入的时候会调用一次 INVISIBLE 接着调用 VISIBLE
            mLuaUserdata.callOnHide();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LuaValue result = mLuaUserdata != null ? mLuaUserdata.callOnBack() : LuaValue.FALSE;
            return result != null && result.optboolean(false);
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mLuaUserdata != null) {
            mLuaUserdata.callOnLayout();
        }
    }

    /**
     * Flexbox account
     */
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
        // diff old and new
        if (mChildNodeViews == childNodeViews) {
            return;
        }

        // remove all the old views
        clearChildNodeViews();

        // set the new nodes
        mChildNodeViews = childNodeViews;

        // enum array and add into it
        generateNodeViewTree();
    }

    void clearChildNodeViews() {
        if (mChildNodeViews == null) {
            return;
        }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            removeView(mChildNodeViews.get(i).getView());
        }

    }

    void generateNodeViewTree() {
        if (mChildNodeViews == null) {
            return;
        }

        int childNodeViewsCount = mChildNodeViews.size();
        for (int i = 0; i < childNodeViewsCount; i++) {
            LuaViewUtil.addView(this, mChildNodeViews.get(i).getView(), null);
        }
    }

}
