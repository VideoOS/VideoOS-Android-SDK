package cn.com.videopls.pub;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.com.venvy.Platform;
import cn.com.venvy.common.bean.LuaFileInfo;
import cn.com.venvy.common.http.HttpStatusPlugin;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by yanjiangbo on 2018/1/18.
 */

public abstract class VideoPlusBaseModel {

    protected final static String TAG = VideoPlusBaseModel.class.getName();

    private Platform mPlatform;
    private BaseRequestConnect mRequestConnect;
    private static HttpStatusPlugin sPlugin = new HttpStatusPlugin();
    private Request mCurrentRequest;


    public VideoPlusBaseModel(@NonNull Platform platform) {
        BaseRequestConnect connect = RequestFactory.initConnect(platform);
        mPlatform = platform;
        mRequestConnect = connect;
        if (needCheckResponseValid()) {
            mRequestConnect.setHttpResponsePlugin(sPlugin);
        }
    }

    public VideoPlusBaseModel(@NonNull Platform platform, @NonNull BaseRequestConnect requestConnect) {
        mPlatform = platform;
        mRequestConnect = requestConnect;
        if (needCheckResponseValid()) {
            mRequestConnect.setHttpResponsePlugin(sPlugin);
        }
    }

    public abstract Request createRequest();

    public abstract IRequestHandler createRequestHandler();

    public boolean needCheckResponseValid() {
        return true;
    }

    public void startRequest() {
        if (mRequestConnect == null) {
            VenvyLog.e(TAG, "connect error, connect can't be null");
            return;
        }
        Request request = createRequest();
        if (request == null) {
            return;
        }
        mRequestConnect.connect(request, createRequestHandler());
        mCurrentRequest = request;
    }

    public void destroy() {
        if (mRequestConnect != null && mCurrentRequest != null) {
            mRequestConnect.abort(mCurrentRequest);
        }
    }

    public Platform getPlatform() {
        return mPlatform;
    }

    public BaseRequestConnect getRequestConnect() {
        return mRequestConnect;
    }

    public List<LuaFileInfo.LuaListBean> luaArray2LuaList(JSONArray luaArray) {
        if(luaArray == null || luaArray.length() <= 0){
            return null;
        }
        List<LuaFileInfo.LuaListBean> videoModeLuaList = new ArrayList<>();
        for (int j = 0; j < luaArray.length(); j++) {
            JSONObject luaFileObj = luaArray.optJSONObject(j);
            if(luaFileObj == null){
                break;
            }
            String luaName = luaFileObj.optString("name");
            String luaMD5 = luaFileObj.optString("md5");
            String luaUrl = luaFileObj.optString("url");
            String luaPath = luaFileObj.optString("path");
            if(TextUtils.isEmpty(luaMD5) || TextUtils.isEmpty(luaUrl)){
                break;
            }
            LuaFileInfo.LuaListBean luaListBean = new LuaFileInfo.LuaListBean();
            luaListBean.setLuaFileMd5(luaMD5);
            luaListBean.setLuaFileName(luaName);
            luaListBean.setLuaFilePath(luaPath);
            luaListBean.setLuaFileUrl(luaUrl);

            videoModeLuaList.add(luaListBean);
        }
        return videoModeLuaList;
    }
}
