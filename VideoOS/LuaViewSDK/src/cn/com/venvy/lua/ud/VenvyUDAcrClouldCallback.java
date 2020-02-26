package cn.com.venvy.lua.ud;

import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.acr.VenvyACRCloudFactory;
import cn.com.venvy.common.interf.IACRCloud;
import cn.com.venvy.lua.view.VenvyLVAcrClouldCallback;

/**
 * Created by lgf on 2020/2/26.
 */

public class VenvyUDAcrClouldCallback extends UDView<VenvyLVAcrClouldCallback> {
    private IACRCloud mAcrCloud;
    LuaValue mAcrCloudCallback;

    public VenvyUDAcrClouldCallback(VenvyLVAcrClouldCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        mAcrCloud = VenvyACRCloudFactory.getACRCloud();
    }

    public VenvyUDAcrClouldCallback setAcrCloudCallback(LuaValue callbacks) {
        if (callbacks != null) {
            mAcrCloudCallback = callbacks;
        }
        return this;
    }

    public void handleAcrMessageBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String message = bundle.getString("data");
        if (TextUtils.isEmpty(message)) {
            return;
        }
        LuaUtil.callFunction(mMqttCallback, JsonUtil.toLuaTable(message));
    }

    public void startRecognize(byte[] buffer) {
        if (mAcrCloud != null) {
            mAcrCloud.startRecognize(buffer);
        }
    }

    public void stopRecognize() {
        if (mAcrCloud != null) {
            mAcrCloud.stopRecognize();
        }
    }

    public void destroyRecognize() {
        if (mAcrCloud != null) {
            mAcrCloud.destroyRecognize();
        }
    }
}
