package cn.com.venvy.common.webview;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.just.agentweb.IWebLayout;

/**
 * Created by videojj_pls on 2019/8/27.
 */

public class VenvyWebLayout extends FrameLayout implements IWebLayout {
    private WebView mWebView;

    public VenvyWebLayout(Context context) {
        super(context);
        init(context);
    }

    public VenvyWebLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VenvyWebLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @NonNull
    @Override
    public ViewGroup getLayout() {
        return this;
    }

    @Nullable
    @Override
    public WebView getWebView() {
        return mWebView;
    }

    private void init(Context context) {
        mWebView = new WebView(context);
        ViewGroup.LayoutParams params = mWebView.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(-1, -1);
        }
        params.width = -1;
        params.height = -1;
        addView(mWebView, params);
    }
}
