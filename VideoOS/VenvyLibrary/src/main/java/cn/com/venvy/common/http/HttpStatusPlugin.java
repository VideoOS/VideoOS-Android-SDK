package cn.com.venvy.common.http;

import android.text.TextUtils;

import org.json.JSONObject;

import cn.com.venvy.common.http.base.HttpExtraPlugin;
import cn.com.venvy.common.interf.Method;

/**
 * Created by mac on 18/1/10.
 */

public class HttpStatusPlugin extends HttpExtraPlugin implements Method<HttpExtraPlugin> {
    private static final String HTTP_TAG_STATUS = "status";

    @Override
    public boolean isSuccess() throws Exception {
        String json = getResponseResult();
        if (TextUtils.isEmpty(json)) {
            return false;
        }
        JSONObject objJson = new JSONObject(json);
        if (!objJson.has(HTTP_TAG_STATUS)) {
            return false;
        }
        int status = objJson.optInt(HTTP_TAG_STATUS);
        return status == 0;
    }

    @Override
    public HttpExtraPlugin call() {
        return this;
    }
}