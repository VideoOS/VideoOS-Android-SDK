package cn.com.venvy.lua.ud;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.com.venvy.common.bean.SocketConnectItem;
import cn.com.venvy.common.bean.SocketUserInfo;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.ISocketConnect;
import cn.com.venvy.common.socket.VenvySocketFactory;
import cn.com.venvy.common.utils.VenvyAesUtil;
import cn.com.venvy.lua.view.VenvyLVMqttCallback;

/**
 * Created by mac on 18/3/29.
 */

public class VenvyUDMqttCallback extends UDView<VenvyLVMqttCallback> {
    LuaValue mMqttCallback;
    private Set<SocketConnectItem> topics;
    private ISocketConnect mSocketConnect;

    public VenvyUDMqttCallback(VenvyLVMqttCallback view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
        mSocketConnect = VenvySocketFactory.getSocketConnect();
    }

    public VenvyUDMqttCallback setMqttCallback(LuaValue callbacks) {
        if (callbacks != null) {
            mMqttCallback = callbacks;
        }
        return this;
    }

    public void setMqttTopics(SocketUserInfo info, Map<String, String> map) {
        if (mSocketConnect == null) {
            return;
        }
        if (map != null) {
            Set<SocketConnectItem> items = new HashSet<>();
            for (String key : map.keySet()) {
                String value = map.get(key);
                SocketConnectItem item = new SocketConnectItem(key, Integer.valueOf(value), this);
                items.add(item);
            }
            topics = items;
            mSocketConnect.startConnect(info, items);
        }
    }

    public void removeTopics() {
        if (mSocketConnect == null) {
            return;
        }
        mSocketConnect.stopConnect(topics);
        topics = null;
    }

    public void handleMqttBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (topics == null || topics.size() <= 0) {
            return;
        }
        String topic = bundle.getString("topic");
        for (SocketConnectItem cacheTopic : topics) {
            if (TextUtils.equals(cacheTopic.topic, topic)) {
                String data = bundle.getString("data");
                if (!DebugStatus.isRelease()) {
                    showNeverAskRationaleDialog(getContext(), data);
                }
                LuaUtil.callFunction(mMqttCallback, JsonUtil.toLuaTable(data));
                return;
            }
        }
    }

    private void showNeverAskRationaleDialog(Context context, String message) {
        if (TextUtils.isEmpty(message) || context == null) {
            return;
        }
        String data = null;
        try {
            JSONObject value = new JSONObject(message);
            String encryptData = value.optString("encryptData");
//            data = VenvyAesUtil.decrypt(encryptData, VenvyAesUtil.AES_KEY, VenvyAesUtil.AES_KEY);
            data = encryptData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(data)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(data);
        new AlertDialog.Builder(context)
                .setTitle("MQTT")
                .setMessage(data)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).create().show();
    }
}
