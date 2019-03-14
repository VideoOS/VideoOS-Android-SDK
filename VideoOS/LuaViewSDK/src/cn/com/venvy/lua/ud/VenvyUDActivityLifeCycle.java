package cn.com.venvy.lua.ud;

import android.os.Bundle;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.observer.ActivityStatusObserver;
import cn.com.venvy.lua.view.VenvyLVActivityCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyUDActivityLifeCycle extends UDView<VenvyLVActivityCallback> {

    private LuaValue mOnActivityStart;
    private LuaValue mOnActivityCreate;
    private LuaValue mOnActivityResume;
    private LuaValue mOnActivityStop;
    private LuaValue mOnActivityPause;
    private LuaValue mOnActivityRestart;
    private LuaValue mOnActivityDestroy;

    public VenvyUDActivityLifeCycle(VenvyLVActivityCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public VenvyUDActivityLifeCycle setPageCallback(LuaTable callbacks) {
        if (callbacks != null) {
            mOnActivityStart = LuaUtil.getFunction(callbacks, "onActivityStart", "onActivityStart");
            mOnActivityCreate = LuaUtil.getFunction(callbacks, "onActivityCreate", "onActivityCreate");
            mOnActivityResume = LuaUtil.getFunction(callbacks, "onActivityResume", "onActivityResume");
            mOnActivityStop = LuaUtil.getFunction(callbacks, "onActivityPause", "onActivityPause");
            mOnActivityPause = LuaUtil.getFunction(callbacks, "onActivityStop", "onActivityStop");
            mOnActivityRestart = LuaUtil.getFunction(callbacks, "onActivityRestart", "onActivityRestart");
            mOnActivityDestroy = LuaUtil.getFunction(callbacks, "onActivityDestroy", "onActivityDestroy");
        }

        return this;
    }


    public VenvyUDActivityLifeCycle setOnActivityStart(LuaValue onActivityStart) {
        this.mOnActivityStart = onActivityStart;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityCreate(LuaValue onActivityCreate) {
        this.mOnActivityCreate = onActivityCreate;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityResume(LuaValue onActivityResume) {
        this.mOnActivityResume = onActivityResume;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityStop(LuaValue onActivityStop) {
        this.mOnActivityStop = onActivityStop;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityPause(LuaValue onActivityStop) {
        this.mOnActivityPause = onActivityStop;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityRestart(LuaValue onActivityRestart) {
        this.mOnActivityRestart = onActivityRestart;
        return this;
    }

    public VenvyUDActivityLifeCycle setOnActivityDestroy(LuaValue onActivityDestroy) {
        this.mOnActivityDestroy = onActivityDestroy;
        return this;
    }

    public void handleActivityBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        int activityStatus = bundle.getInt("activity_status");
        LuaValue activityMethodName = null;
        switch (activityStatus) {
            case ActivityStatusObserver.STATUS_CREATE:
                activityMethodName = mOnActivityCreate;
                break;
            case ActivityStatusObserver.STATUS_START:
                activityMethodName = mOnActivityStart;
                break;
            case ActivityStatusObserver.STATUS_RESUME:
                activityMethodName = mOnActivityResume;
                break;
            case ActivityStatusObserver.STATUS_RESTART:
                activityMethodName = mOnActivityRestart;
                break;
            case ActivityStatusObserver.STATUS_PAUSE:
                activityMethodName = mOnActivityPause;
                break;
            case ActivityStatusObserver.STATUS_DESTROY:
                activityMethodName = mOnActivityDestroy;
                break;
            case ActivityStatusObserver.STATUS_STOP:
                activityMethodName = mOnActivityStop;
                break;
        }

        LuaUtil.callFunction(activityMethodName);
    }
}
