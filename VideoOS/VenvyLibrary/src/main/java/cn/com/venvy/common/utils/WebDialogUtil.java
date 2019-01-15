package cn.com.venvy.common.utils;

import android.content.Context;

import cn.com.venvy.common.webview.WebViewDialog;

/**
 * 打开全屏webView
 * Created by Arthur on 2017/7/21.
 */

public class WebDialogUtil {
    /**
     * 芒果专用方法
     *
     * @param url
     */
    public static void openWebDialog(Context context, String url) {
        WebViewDialog webViewDialog = WebViewDialog.getInstance(context);
        webViewDialog.loadUrl(url);
        webViewDialog.show();
    }
}
