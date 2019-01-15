package cn.com.venvy.lua.bridge;

import android.text.TextUtils;

import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.io.File;

import cn.com.venvy.Platform;
import cn.com.venvy.common.http.FileUploadHelper;
import cn.com.venvy.common.http.HttpRequest;
import cn.com.venvy.common.http.PostFormRequest;
import cn.com.venvy.common.http.RequestFactory;
import cn.com.venvy.common.http.base.BaseRequestConnect;
import cn.com.venvy.common.http.base.IRequestHandler;
import cn.com.venvy.common.http.base.IResponse;
import cn.com.venvy.common.http.base.Request;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;


/**
 * native:get("http://www.baidu.com",{},{},function(data)
 * print(data)
 * end)
 * Created by Arthur on 2017/8/15.
 */

public class LVHttpBridge {
    private BaseRequestConnect mBaseRequestConnect;
    private FileUploadHelper mUploadHelper;
    private Platform platform;

    public LVHttpBridge(Platform platform) {
        this.platform = platform;
    }

    public int get(String url, LuaTable table, LuaFunction callback) {
        Request request = HttpRequest.get(url, LuaUtil.toMap(table));
        startConnect(request, callback);
        return request.mRequestId;
    }

    public int delete(String url, LuaTable table, LuaFunction callback) {
        Request request = HttpRequest.delete(url, LuaUtil.toMap(table));
        startConnect(request, callback);
        return request.mRequestId;
    }

    public int put(String url, LuaTable table, LuaFunction callback) {
        Request request = HttpRequest.put(url, LuaUtil.toMap(table));
        startConnect(request, callback);
        return request.mRequestId;
    }

    public int post(String url, LuaTable table, LuaFunction callback) {
        Request request = HttpRequest.post(url, LuaUtil.toMap(table));
        startConnect(request, callback);
        return request.mRequestId;
    }

    //文件上传用
    public void upload(final String url, String filePath, final LuaFunction callback) {
        File file = new File(filePath);
        if (!file.exists()) {
            LuaUtil.callFunction(callback, LuaValue.NIL);
            return;
        }

        final PostFormRequest.FileInfo fileInfo = new PostFormRequest.FileInfo();
        fileInfo.file = file;
        fileInfo.fileName = filePath;
        fileInfo.name = "file";
        if (mUploadHelper == null) {
            mUploadHelper = new FileUploadHelper();
        }
        mUploadHelper.uploadAsync(PostFormRequest.upload(fileInfo, url), new FileUploadHelper.IUploadListener() {

            @Override
            public void uploadComplete(String data) {
                VenvyLog.i("--图片上传结果--" + data);
                LuaUtil.callFunction(callback, TextUtils.isEmpty(data) ? LuaValue.NIL : LuaValue.valueOf(data));
            }
        });
    }

    private void startConnect(Request request, final LuaFunction callback) {
        if (platform == null) {
            return;
        }
        if (mBaseRequestConnect == null) {
            mBaseRequestConnect = RequestFactory.initConnect(platform);
        }
        mBaseRequestConnect.connect(request, new IRequestHandler() {
            @Override
            public void requestFinish(Request request, IResponse response) {
                if (callback == null) {
                    return;
                }
                if (response.isSuccess()) {
                    String data = response.getResult();
                    final LuaValue table = JsonUtil.toLuaTable(data);
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            LuaUtil.callFunction(callback, table, LuaValue.NIL);
                        }
                    });
                } else {
                    requestError(request, new Exception("http not successful"));
                }
            }

            @Override
            public void requestError(Request request, final Exception e) {
                if (callback == null) {
                    return;
                }
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        LuaUtil.callFunction(callback, LuaValue.NIL, LuaValue.valueOf(e != null && e.getMessage() != null ? e.getMessage() : "unkown error"));
                    }
                });

            }

            @Override
            public void startRequest(Request request) {

            }

            @Override
            public void requestProgress(Request request, int progress) {

            }
        });
    }

    public boolean abortAll() {
        if (mUploadHelper != null) {
            mUploadHelper.onDestroy();
        }
        return mBaseRequestConnect != null && mBaseRequestConnect.abortAll();
    }

    public boolean abort(int requestId) {
        return mBaseRequestConnect != null && mBaseRequestConnect.abort(requestId);
    }
}
