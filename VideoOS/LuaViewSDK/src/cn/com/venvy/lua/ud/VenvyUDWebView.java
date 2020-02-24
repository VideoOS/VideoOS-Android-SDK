package cn.com.venvy.lua.ud;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.ValueCallback;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.webview.IVenvyWebView;
import cn.com.venvy.common.webview.JsBridge;
import cn.com.venvy.lua.view.VenvyLVWebView;

/**
 * Created by Arthur on 2017/9/4.
 */

public class VenvyUDWebView extends UDView<VenvyLVWebView> {

    private JsBridge jsBridge;

    public VenvyUDWebView(VenvyLVWebView view, Globals globals, LuaValue metatable, Varargs initParams) {
        super(view, globals, metatable, initParams);
    }

    public void setJsBridge(JsBridge jsBridge) {
        this.jsBridge = jsBridge;
    }

    /**
     * Loads the given URL.
     */
    public VenvyUDWebView loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            IVenvyWebView view;
            if (this.getView() != null && (view = this.getView().getWebView()) != null) {
                view.loadUrl(url);
            }
        }

        return this;
    }

    public boolean canGoBack() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.canGoBack();
        }
        return false;
    }

    public boolean canGoForward() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.canGoBack();
        }
        return false;
    }

    public VenvyUDWebView goBack() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.goBack();
        }

        return this;
    }

    public VenvyUDWebView goForward() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.goForward();
        }

        return this;
    }

    public VenvyUDWebView reload() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.reload();
        }

        return this;
    }

    public VenvyUDWebView stopLoading() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.stopLoading();
        }

        return this;
    }

    public boolean isLoading() {
        return this.getView() != null && this.getView().getLoadingState();
    }

    /**
     * Get the tile of web page
     */
    public String title() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.getTitle();
        }

        return "";
    }

    /**
     * Get the loaded URL
     */
    public String url() {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            return view.getUrl();
        }

        return "";
    }

    @Override
    public UDView setCallback(LuaValue callbacks) {
        this.mCallback = callbacks;
        return this;
    }

    /**
     * 设置WebView的enabled state
     */
    @Override
    public UDView setEnabled(boolean enable) {
        IVenvyWebView view;
        if (this.getView() != null && (view = this.getView().getWebView()) != null) {
            view.setEnabled(enable);
        }
        return this;
    }

    /**
     * 设置SwipeRefreshLayout是否有效,无效则不可下拉
     */
    public VenvyUDWebView pullRefreshEnable(boolean enable) {
        final VenvyLVWebView view = this.getView();
        if (view != null) {
            view.setEnabled(enable);
        }

        return this;
    }

    public boolean isPullRefreshEnable() {
        return this.getView() != null && this.getView().isEnabled();
    }

    public String callJS(String jsMethod, final LuaFunction callback) {
        IVenvyWebView webView;
        if (getView() == null || (webView = this.getView().getWebView()) == null) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(jsMethod, new ValueCallback<String>() {
                /**
                 *
                 * @param value  js的函数执行的返回值
                 */
                @Override
                public void onReceiveValue(String value) {
                    LuaValue luaValue = LuaValue.valueOf(value);
                    LuaUtil.callFunction(callback, luaValue);
                }
            });
        } else {
            webView.loadUrl(jsMethod);
        }


        return "";
    }

    public VenvyUDWebView webViewCallback(final LuaValue callback){
        jsBridge.setWebViewCloseListener(new JsBridge.WebViewCloseListener() {
            @Override
            public void onClose(CloseType actionType) {
                LuaUtil.callFunction(callback);
            }
        });
        return this;
    }

    public VenvyUDWebView setInitData(String data){
        final VenvyLVWebView view = this.getView();
        if (view != null) {
            view.setJsData(data);
        }
        return this;
    }

    public void setZoomScale(float scale) {
        final VenvyLVWebView view = this.getView();
        if (view != null) {
            view.setZoomScale(scale);
        }
    }

    public void disableDeepLink(boolean disableDeepLink) {
        final VenvyLVWebView view = this.getView();
        if (view != null) {
            view.disableDeepLink(disableDeepLink);
        }
    }
}

