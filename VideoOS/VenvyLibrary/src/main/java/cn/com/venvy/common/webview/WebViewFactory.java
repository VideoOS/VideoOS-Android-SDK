package cn.com.venvy.common.webview;

import android.content.Context;

import java.lang.reflect.Constructor;

import cn.com.venvy.VenvyRegisterLibsManager;
import cn.com.venvy.common.utils.VenvyLog;

/**
 * Created by mac on 18/2/5.
 */

public class WebViewFactory {
    public static IVenvyWebView createWebView(Context context) {
        Class<? extends IVenvyWebView> clas = VenvyRegisterLibsManager.getWebViewLib();
        if (clas != null) {
            Constructor constructor = null;
            try {
                constructor = clas.getDeclaredConstructor(Context.class);
                IVenvyWebView webView = (IVenvyWebView) constructor.newInstance(context);
                return webView;
            } catch (Exception e) {
                return new VenvyWebView(context);
            }
        }
        return new VenvyWebView(context);
    }
}
