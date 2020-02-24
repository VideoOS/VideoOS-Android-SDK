package cn.com.venvy.common.webview;

import android.webkit.ValueCallback;

/**
 * Created by mac on 18/2/8.
 */

public interface IVenvyWebView {
    void addJavascriptInterface(Object object, String name);

    void callJsFunction(String functionName, String data);

    void setWebViewClient(IVenvyWebViewClient webViewClient);

    void setWebChromeClient(IVenvyWebChromeClient webChromeClient);

    void loadUrl(String url);

    boolean canGoBack();

    void goBack();

    void goForward();

    void destroy();

    void reload();

    void stopLoading();

    void setZoomScale(float scale);

    void disableDeepLink(boolean disableDeepLink);

    String getTitle();

    String getUrl();

    void setEnabled(boolean enabled);

    void evaluateJavascript(String script, ValueCallback<String> resultCallback);

    void showErrorPage(String showErrorPage);

    void updateNaviTitle(String updateNaviTitle);

    void openApplet(String openApplet);

    int getWebViewX();

    int getWebViewY();
}
