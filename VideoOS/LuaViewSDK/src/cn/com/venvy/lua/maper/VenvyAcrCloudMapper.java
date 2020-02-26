package cn.com.venvy.lua.maper;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewMethodMapper;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.io.File;
import java.util.List;

import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.ud.VenvyUDAcrClouldCallback;

/**
 * Created by mac on 18/3/29.
 */
@LuaViewLib(revisions = {"20200226已对标"})
public class VenvyAcrCloudMapper<U extends VenvyUDAcrClouldCallback> extends UIViewMethodMapper<U> {
    private static final String TAG = "VenvyAcrCloudMapper";
    private static final String[] sMethods = new String[]{
            "acrRecognizeCallback",
            "startAcrRecognize",
            "stopAcrRecognize",
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return acrRecognizeCallback(target, varargs);
            case 1:
                return startAcrRecognize(target, varargs);
            case 2:
                return stopAcrRecognize(target, varargs);
        }
        return super.invoke(code, target, varargs);
    }

    public LuaValue acrRecognizeCallback(U target, Varargs varargs) {
        final LuaFunction callback = varargs.optfunction(2, null);
        if (callback != null && callback.isfunction()) {
            return target.setMqttCallback(callback);
        }
        return LuaValue.NIL;
    }

    public LuaValue startAcrRecognize(U target, Varargs args) {
        try {
            if (args.narg() > 0) {
                String filePath = LuaUtil.getString(args, 2);
                if (TextUtils.isEmpty(filePath)) {
                    VenvyLog.e(VenvyAcrCloudMapper.class.getName(), new NullPointerException("startAcrRecognize is error,because recognize url is null"));
                    return LuaValue.NIL;
                }
                File file=new File(filePath);
                if(!file.exists()||!file.isFile()){
                    VenvyLog.e(VenvyAcrCloudMapper.class.getName(), new NullPointerException("startAcrRecognize is error,because recognize url is null"));
                    return LuaValue.NIL;
                }
                target.startRecognize(VenvyFileUtil.readBytes(file));
            }
        } catch (Exception e) {
            VenvyLog.e(VenvyAcrCloudMapper.class.getName(), e);
        }
        return LuaValue.NIL;
    }

    public LuaValue stopAcrRecognize(U target, Varargs args) {
        try {
            target.stopRecognize();
        } catch (Exception e) {
            VenvyLog.e(VenvyAcrCloudMapper.class.getName(), e);
        }
        return LuaValue.NIL;
    }
}
