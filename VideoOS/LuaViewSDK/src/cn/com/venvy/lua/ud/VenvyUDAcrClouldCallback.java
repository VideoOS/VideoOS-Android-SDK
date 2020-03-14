package cn.com.venvy.lua.ud;

import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.Platform;
import cn.com.venvy.common.acr.VenvyACRCloudFactory;
import cn.com.venvy.common.bean.AcrConfigInfo;
import cn.com.venvy.common.interf.IACRCloud;
import cn.com.venvy.common.interf.IPlatformRecordInterface;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.view.VenvyLVAcrClouldCallback;

/**
 * Created by lgf on 2020/2/26.
 */

public class VenvyUDAcrClouldCallback extends UDView<VenvyLVAcrClouldCallback> {
    private static final String TAG = VenvyUDAcrClouldCallback.class.getName();
    private IACRCloud mAcrCloud;
    LuaValue mAcrCloudCallback;
    private Platform mPlatform;

    public VenvyUDAcrClouldCallback(Platform platform, VenvyLVAcrClouldCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        mAcrCloud = VenvyACRCloudFactory.getACRCloud();
        this.mPlatform = platform;
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
        LuaUtil.callFunction(mAcrCloudCallback, JsonUtil.toLuaTable(message));
    }

    public void startRecognize(AcrConfigInfo info, byte[] buffer) {
        if (mAcrCloud != null) {
            mAcrCloud.startRecognize(info, buffer);
        }
    }

    public void stopRecognize() {
        if (mAcrCloud != null) {
            mAcrCloud.stopRecognize();
        }
    }

    public void acrRecordStart() {
        if (mPlatform == null) {
            return;
        }
        IPlatformRecordInterface platformRecord = mPlatform.getPlatformRecordInterface();
        if (platformRecord == null) {
            VenvyLog.i(TAG, "start acrRecordStart error,because VideoPlusAdapter no interface buildRecordInterface");
            return;
        }
        platformRecord.startRecord();
    }

    public void acrRecordEnd(final LuaFunction callback) {
        if (mPlatform == null || callback == null) {
            return;
        }
        IPlatformRecordInterface platformRecord = mPlatform.getPlatformRecordInterface();
        if (platformRecord == null) {
            VenvyLog.i(TAG, "start acrRecordStart error,because VideoPlusAdapter no interface buildRecordInterface");
            return;
        }
        platformRecord.endRecord(new IPlatformRecordInterface.RecordCallback() {
            @Override
            public void onRecordResult(String filePath) {
                LuaUtil.callFunction(callback, LuaValue.valueOf(filePath));
            }
        });
    }

    public void destroyRecognize() {
        if (mAcrCloud != null) {
            mAcrCloud.destroyRecognize();
        }
    }
}
