package cn.com.venvy.common.webview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;


/**
 * Created by Arthur on 2017/7/13.
 */

public class VenvyWebView extends FrameLayout implements IVenvyWebView {
    private AgentWeb mAgentWeb;

    public VenvyWebView(Context context) {
        super(context);
        init(context);
    }

    public VenvyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VenvyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setJsBridge(JsBridge bridge) {
        if (mAgentWeb != null) {
            mAgentWeb.getJsInterfaceHolder().addJavaObject("Applet", bridge);
        }
    }

    @Override
    public void setWebViewClient(IVenvyWebViewClient webViewClient) {

    }

    @Override
    public void setWebChromeClient(IVenvyWebChromeClient webChromeClient) {

    }


    @Override
    public boolean canGoBack() {
        if (mAgentWeb == null || mAgentWeb.getWebCreator() == null) {
            return false;
        }
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        return webView != null ? webView.canGoBack() : false;
    }

    @Override
    public void goBack() {
        if (mAgentWeb != null) {
            mAgentWeb.back();
        }
    }

    @Override
    public void goForward() {
        if (mAgentWeb == null || mAgentWeb.getWebCreator() == null || mAgentWeb.getWebCreator().getWebView() == null) {
            return;
        }
        mAgentWeb.getWebCreator().getWebView().goForward();
    }

    @Override
    public void destroy() {
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
        }
    }

    @Override
    public void reload() {
        if (mAgentWeb != null) {
            mAgentWeb.getUrlLoader().reload();
        }
    }

    @Override
    public void stopLoading() {
        if (mAgentWeb != null) {
            mAgentWeb.getUrlLoader().stopLoading();
        }
    }

    @Override
    public String getTitle() {
        if (mAgentWeb == null || mAgentWeb.getWebCreator() == null) {
            return null;
        }
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        return webView != null ? webView.getTitle() : null;
    }

    @Override
    public String getUrl() {
        if (mAgentWeb == null || mAgentWeb.getWebCreator() == null) {
            return null;
        }
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        return webView != null ? webView.getUrl() : null;
    }

    @Override
    public void loadUrl(String url) {
        if (mAgentWeb != null && !TextUtils.isEmpty(url)) {
            mAgentWeb.getUrlLoader().loadUrl(url);
        }
    }

    @Override
    public void addJavascriptInterface(Object object, String name) {
        if (mAgentWeb != null) {
            mAgentWeb.getJsInterfaceHolder().addJavaObject(object.toString(), name);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void evaluateJavascript(String script, ValueCallback<String> resultCallback) {

    }

    @Override
    public void callJsFunction(String functionName, String data) {
        if (mAgentWeb != null) {
            mAgentWeb.getJsAccessEntrace().callJs("javascript:" + functionName + "('" + data + "')");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    private void init(Context context) {
        if (context instanceof Activity) {
            mAgentWeb = AgentWeb.with((Activity) context)
                    .setAgentWebParent(this, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                    .useDefaultIndicator()
                    .setWebChromeClient(mWebChromeClient)
                    .setWebViewClient(mWebViewClient)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setWebLayout(new VenvyWebLayout(context))
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                    .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                    .createAgentWeb().ready().get();
        }
    }

    private com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

        }
    };
    private com.just.agentweb.WebChromeClient mWebChromeClient = new com.just.agentweb.WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    };
}
