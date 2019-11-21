package cn.com.venvy.lua.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVNativeViewProvider;
import com.taobao.luaview.view.interfaces.ILVView;

import org.json.JSONObject;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import cn.com.venvy.Platform;
import cn.com.venvy.common.webview.IVenvyWebView;
import cn.com.venvy.common.webview.IVenvyWebViewClient;
import cn.com.venvy.common.webview.JsBridge;
import cn.com.venvy.common.webview.VenvyWebView;
import cn.com.venvy.common.webview.WebViewFactory;
import cn.com.venvy.lua.ud.VenvyUDWebView;
import cn.com.venvy.processor.annotation.VenvyAutoData;


/**
 * Created by Arthur on 2017/9/4.
 */

public class VenvyLVWebView extends FrameLayout implements ILVNativeViewProvider, ILVView {

    protected UDView mLuaUserdata;
    protected IVenvyWebView mWebView;
    protected JsBridge mJsBridge;
    protected boolean mIsLoading;
    protected Platform platform;

    public VenvyLVWebView(Platform platform, Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.platform = platform;
        this.mLuaUserdata = new VenvyUDWebView(this, globals, metaTable, varargs);
    }

    public void init(Context context, Globals globals) {
        this.mWebView = WebViewFactory.createWebView(context);
        mJsBridge = new JsBridge(context, mWebView, platform);
        mJsBridge.setDeveloperUserId(getDeveloperUserId(globals));
        if (mWebView instanceof VenvyWebView) {
            ((VenvyWebView) mWebView).setJsBridge(mJsBridge);
        }
        if (mWebView instanceof View) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((View) mWebView).setLayoutParams(params);
            addView((View) mWebView);
        }
        mWebView.setWebViewClient(new IVenvyWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(View webView, String url) {
                return false;
            }

            @Override
            public void onPageStarted(View webView, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(View webView, String url) {

            }

            @Override
            public void onReceivedError(View webView, int errorCode, String description, String failingUrl) {

            }
        });

        this.setEnabled(true);
    }

    public IVenvyWebView getWebView() {
        return mWebView;
    }

    public boolean getLoadingState() {
        return mIsLoading;
    }


    @Override
    public View getNativeView() {
        if (mWebView instanceof View) {
            return (View) this.getWebView();
        }

        return null;
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    private Map getViewPriority(Globals globals) {
        try {
            ViewParent view = globals.container.getParent();
            if (view != null) {
                Field field = view.getClass().getDeclaredField("data");
                if (field != null && field.getAnnotation(VenvyAutoData.class) != null) {
                    field.setAccessible(true);
                    Object targetPriority = field.get(view);
                    if (targetPriority != null && targetPriority instanceof Map) {
                        return (Map) targetPriority;
                    }
                }
            }
        } catch (Exception e) {
            //忽略此处异常
        }
        return new HashMap();
    }

    /***
     * JS交互参数 获取开发者id
     * @param globals
     * @return
     */
    private String getDeveloperUserId(Globals globals) {
        String developerUserId = null;
        Map<String, String> dataParams = getViewPriority(globals);
        if (dataParams != null || dataParams.size() <= 0) {
            return developerUserId;
        }
        try {
            JSONObject dataObj = new JSONObject(dataParams.get("miniAppInfo"));
//            JSONObject miniAppInfoObj = dataObj.optJSONObject("miniAppInfo");
            developerUserId = dataObj.optString("developerUserId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return developerUserId;
    }
}
