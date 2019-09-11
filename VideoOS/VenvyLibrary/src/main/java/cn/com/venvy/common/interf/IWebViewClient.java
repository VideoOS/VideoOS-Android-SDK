package cn.com.venvy.common.interf;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

/**
 * Created by videojj_pls on 2019/9/2.
 */

public interface IWebViewClient {
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request);

    @Nullable
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request);

    public boolean shouldOverrideUrlLoading(final WebView view, String url);

    public void onPageStarted(WebView view, String url, Bitmap favicon);

    public void onPageFinished(WebView view, String url);

    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse);

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error);

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
}
