package cn.com.venvy.common.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Created by Arthur on 2017/7/13.
 */

public class VenvyWebView extends WebView implements IVenvyWebView {

    public VenvyWebView(Context context) {
        super(context);
        init();
    }

    public VenvyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        buildSetting();
    }

    private void buildSetting() {
        clearCache(true);
        clearHistory();
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDefaultTextEncodingName("utf-8");// 避免中文乱码
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setNeedInitialFocus(false);
//        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
//        webSettings.setBlockNetworkLoads(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);//1、提高渲染的优先级
//        webSettings.setBlockNetworkImage(true);//把图片加载放在最后来加载渲染
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(false);

    }


    @Override
    public void setWebViewClient(final IVenvyWebViewClient webViewClient) {
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                return webViewClient != null && webViewClient.shouldOverrideUrlLoading(webView, url);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon) {
                if (webViewClient != null) {
                    webViewClient.onPageStarted(webView, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                if (webViewClient != null) {
                    webViewClient.onPageFinished(webView, url);
                }
            }

            @Override
            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                if (webViewClient != null) {
                    webViewClient.onReceivedError(webView, errorCode, description, failingUrl);
                }
            }
        });
    }

    @Override
    public void setWebChromeClient(final IVenvyWebChromeClient webChromeClient) {
        setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (webChromeClient != null) {
                    webChromeClient.onProgressChanged(view, newProgress);
                }
            }
        });
    }
}
