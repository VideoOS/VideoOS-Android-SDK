package cn.com.venvy.common.webview;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by mac on 18/2/8.
 */

public interface IVenvyWebViewClient {
    boolean shouldOverrideUrlLoading(View webView, String url);

    void onPageStarted(View webView, String url, Bitmap favicon);

    void onPageFinished(View webView, String url);

    void onReceivedError(View webView, int errorCode, String description, String failingUrl);
}
