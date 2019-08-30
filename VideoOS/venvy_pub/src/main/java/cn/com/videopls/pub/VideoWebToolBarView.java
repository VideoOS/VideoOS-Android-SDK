package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by Lucas on 2019/8/30.
 */
public class VideoWebToolBarView extends BaseVideoVisionView {


    private WebView webView;

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

            }
        });

    }

    public void setAdapter(VideoPlusAdapter adapter) {
        controller.setAdapter(adapter);
    }


    private void initWebView() {
        webView = new WebView(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.topMargin = VenvyUIUtil.dip2px(getContext(), 44f);
        webView.setLayoutParams(layoutParams);

        webView.setWebViewClient(new WebViewClient());
        addView(webView);
    }


    public void fetchTargetUrl(String appletId) {
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
