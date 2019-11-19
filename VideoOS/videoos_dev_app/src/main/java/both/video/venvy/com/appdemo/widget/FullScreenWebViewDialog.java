package both.video.venvy.com.appdemo.widget;

import android.app.Dialog;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import both.video.venvy.com.appdemo.R;

/**
 * Created by videojj_pls on 2018/9/27.
 */

public class FullScreenWebViewDialog extends Dialog {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏大小
        WindowManager.LayoutParams params = getWindow().getAttributes();
        //设置宽
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置宽
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        //
        getWindow().setAttributes(params);
    }

    private FullScreenWebViewDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    public static synchronized FullScreenWebViewDialog getInstance(Context context) {
        FullScreenWebViewDialog instance = new FullScreenWebViewDialog(context, R.style.style_dialog);
        return instance;
    }

    public FullScreenWebViewDialog loadUrl(String url) {
        if (mWebView != null && !TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
        show();
        return this;
    }

    /***
     * 关闭执行方法
     */
    @Override
    public void dismiss() {
        if (!this.isShowing() && mWebView != null) {
            mWebView.setWebChromeClient(null);
            mWebView.destroy();
        }
        super.dismiss();
    }

    private void initView(final Context context) {
        View parent = LayoutInflater.from(context).inflate(R.layout.layout_fullscreen_web, null, false);
        mWebView = (WebView) parent.findViewById(R.id.web);
        buildSetting(mWebView);
        final ProgressBar bar = (ProgressBar) parent.findViewById(R.id.bar);
        bar.setProgress(0);
        ImageView backView = (ImageView) parent.findViewById(R.id.back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    bar.setVisibility(View.GONE);
                }
            }
        });
        setContentView(parent);
    }

    private void buildSetting(WebView webView) {
        webView.clearCache(true);
        webView.clearHistory();
        WebSettings webSettings = webView.getSettings();
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
}
