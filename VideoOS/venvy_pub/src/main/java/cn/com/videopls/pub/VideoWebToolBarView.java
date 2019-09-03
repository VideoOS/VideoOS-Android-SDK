package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.HashMap;

import cn.com.venvy.common.interf.IJsParamsCallback;
import cn.com.venvy.common.interf.IWebViewClient;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.common.webview.VenvyWebView;

/**
 * Created by Lucas on 2019/8/30.
 */
public class VideoWebToolBarView extends BaseVideoVisionView {


    private VenvyWebView webView;

    private String appletId;

    private VideoPlusH5Controller controller;

    public VideoWebToolBarView(Context context) {
        super(context);
        init();
    }

    public VideoWebToolBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoWebToolBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoWebToolBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        controller = new VideoPlusH5Controller(getContext(), this);

        retryContent.setVisibility(GONE);
        errorContent.setVisibility(GONE);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initWebView();

        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, appletId);
                ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLOSE_H5_VISION_PROGRAM, bundle);
            }
        });

        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

    }

    public void setAdapter(VideoPlusAdapter adapter) {
        controller.setAdapter(adapter);
    }


    private void initWebView() {
        webView = new VenvyWebView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = VenvyUIUtil.dip2px(getContext(), 44f);
        webView.setLayoutParams(layoutParams);
        webView.setJsParamsCallback(new IJsParamsCallback() {
            @Override
            public void showErrorPage(String showErrorPage) {

            }

            @Override
            public void updateNaviTitle(String updateNaviTitle) {
                if (!TextUtils.isEmpty(updateNaviTitle)) {
                    tvTitle.setText(updateNaviTitle);
                }
            }

            @Override
            public void openApplet(String openApplet) {
                VenvyLog.d("openApplet : " + openApplet);

                if (TextUtils.isEmpty(openApplet)) return;

                Uri uri = Uri.parse(openApplet);
                controller.navigation(uri, new HashMap<String, String>(), new IRouterCallback() {
                    @Override
                    public void arrived() {

                    }

                    @Override
                    public void lost() {

                    }
                });
            }
        });
        webView.setWebViewClient(new IWebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                VenvyLog.d("H5 onPageFinished : " + url);
                ivBack.setVisibility(webView.canGoBack() ? VISIBLE : INVISIBLE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
        });

        addView(webView);
    }


    public void fetchTargetUrl(String appletId,String data) {
        this.appletId = appletId;
        loadingContent.setVisibility(VISIBLE);
        startLoadingAnimation();
        controller.startH5Program(appletId);
    }

    public void openLink(final String url) {
        VenvyLog.d("openLink : " + url);
        webView.loadUrl(url);

        loadingContent.setVisibility(GONE);
        cancelLoadingAnimation();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (webView != null) {
            removeView(webView);
            webView.destroy();
        }
    }
}
