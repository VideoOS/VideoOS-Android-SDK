package cn.com.venvy.common.webview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import cn.com.venvy.common.agentweb.common.CommonWebChromeClient;
import cn.com.venvy.common.interf.IJsParamsCallback;
import cn.com.venvy.common.interf.IWebViewClient;


/**
 * Created by Arthur on 2017/7/13.
 */

public class VenvyWebView extends FrameLayout implements IVenvyWebView {
    private AgentWeb mAgentWeb;
    private IJsParamsCallback mIJsParamsCallback;
    private IWebViewClient mIwebViewClient;

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
                    .setWebChromeClient(new CommonWebChromeClient())
                    .setWebViewClient(mWebViewClient)
                    .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                    .setWebLayout(new VenvyWebLayout(context))
                    .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                    .interceptUnkownUrl() //拦截找不到相关页面的Scheme
                    .createAgentWeb().ready().get();
        }
    }

    @Override
    public void showErrorPage(String showErrorPage) {
        if (mIJsParamsCallback != null) {
            mIJsParamsCallback.showErrorPage(showErrorPage);
        }
    }

    @Override
    public void updateNaviTitle(String updateNaviTitle) {
        if (mIJsParamsCallback != null) {
            mIJsParamsCallback.updateNaviTitle(updateNaviTitle);
        }
    }

    @Override
    public void openApplet(String openApplet) {
        if (mIJsParamsCallback != null) {
            mIJsParamsCallback.openApplet(openApplet);
        }
    }

    public void setJsParamsCallback(IJsParamsCallback callback) {
        this.mIJsParamsCallback = callback;
    }

    public IJsParamsCallback getIJsParamsCallback() {
        return mIJsParamsCallback;
    }

    public void setWebViewClient(IWebViewClient webViewClient) {
        this.mIwebViewClient = webViewClient;
    }

    public IWebViewClient getIWebViewClient() {
        return mIwebViewClient;
    }

    private com.just.agentweb.WebViewClient mWebViewClient = new com.just.agentweb.WebViewClient() {

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onReceivedError(view, request, error);
            } else {
                super.onReceivedError(view, request, error);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (mIwebViewClient != null) {
                return mIwebViewClient.shouldOverrideUrlLoading(view, request);
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (mIwebViewClient != null) {
                return mIwebViewClient.shouldInterceptRequest(view, request);
            }
            return super.shouldInterceptRequest(view, request);
        }

        //
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            //优酷想唤起自己应用播放该视频 ， 下面拦截地址返回 true  则会在应用内 H5 播放 ，禁止优酷唤起播放该视频， 如果返回 false ， DefaultWebClient  会根据intent 协议处理 该地址 ， 首先匹配该应用存不存在 ，如果存在 ， 唤起该应用播放 ， 如果不存在 ， 则跳到应用市场下载该应用 .
            /*else if (isAlipay(view, mUrl))   //1.2.5开始不用调用该方法了 ，只要引入支付宝sdk即可 ， DefaultWebClient 默认会处理相应url调起支付宝
                return true;*/
            if (mIwebViewClient != null) {
                return mIwebViewClient.shouldOverrideUrlLoading(view, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onPageStarted(view, url, favicon);
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onPageFinished(view, url);
            } else {
                super.onPageFinished(view, url);
            }

        }
        /*错误页回调该方法 ， 如果重写了该方法， 上面传入了布局将不会显示 ， 交由开发者实现，注意参数对齐。*/
       /* public void onMainFrameError(AbsAgentWebUIController agentWebUIController, WebView view, int errorCode, String description, String failingUrl) {

            Log.i(TAG, "AgentWebFragment onMainFrameError");
            agentWebUIController.onMainFrameError(view,errorCode,description,failingUrl);

        }*/

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onReceivedHttpError(view, request, errorResponse);
            } else {
                super.onReceivedHttpError(view, request, errorResponse);
            }

//			Log.i(TAG, "onReceivedHttpError:" + 3 + "  request:" + mGson.toJson(request) + "  errorResponse:" + mGson.toJson(errorResponse));
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onReceivedSslError(view, handler, error);
            } else {
                handler.proceed();
                super.onReceivedSslError(view, handler, error);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (mIwebViewClient != null) {
                mIwebViewClient.onReceivedError(view, errorCode, description, failingUrl);
            } else {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

//			Log.i(TAG, "onReceivedError:" + errorCode + "  description:" + description + "  errorResponse:" + failingUrl);
        }
    };
}
