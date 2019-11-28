package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
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

import org.json.JSONException;
import org.json.JSONObject;

import cn.com.venvy.common.interf.IJsParamsCallback;
import cn.com.venvy.common.interf.IWebViewClient;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;
import cn.com.venvy.common.webview.JsBridge;
import cn.com.venvy.common.webview.VenvyWebView;

/**
 * Created by Lucas on 2019/8/30.
 */
public class VideoWebToolBarView extends BaseVideoVisionView {


    private VenvyWebView webView;

    private String appletId;

    private VideoPlusH5Controller controller;

    private JsBridge jsBridge;


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
        ivBack.setVisibility(INVISIBLE);
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
        webView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        webView.setJsParamsCallback(new IJsParamsCallback() {
            @Override
            public void showErrorPage(final String showErrorPage) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        errorContent.setVisibility(VISIBLE);
                        String message = "";
                        if (TextUtils.isEmpty(showErrorPage)) {
                            message = getContext().getString(
                                    VenvyResourceUtil.getStringId(getContext(), "errorDesc"));
                        } else {
                            try {
                                message = new JSONObject(showErrorPage).getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        tvErrorMsg.setText(message);
                        webView.setVisibility(GONE);
                    }
                });
            }

            @Override
            public void updateNaviTitle(final String updateNaviTitle) {
                VenvyUIUtil.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(updateNaviTitle)) {
                            try {
                                tvTitle.setText(new JSONObject(updateNaviTitle).getString("title"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }

            @Override
            public void openApplet(String openApplet) {
                // {"appletId":"1","screenType":1,"appType":2,"data":"data"}
                VenvyLog.d("openApplet : " + openApplet);

                if (TextUtils.isEmpty(openApplet)) return;

                try {
                    JSONObject jsonObject = new JSONObject(openApplet);
                    String appletId = jsonObject.getString("appletId");
                    String screenType = jsonObject.getString("screenType");
                    String appType = jsonObject.getString("appType");

                    // 拉起一个对应的容器
                    Bundle bundle = new Bundle();
                    bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, appletId);
                    bundle.putString(VenvyObservableTarget.KEY_ORIENTATION_TYPE, screenType);
                    bundle.putString(VenvyObservableTarget.Constant.CONSTANT_APP_TYPE, appType);

                    if (jsonObject.has("data")) {
                        bundle.putString(VenvyObservableTarget.Constant.CONSTANT_DATA, jsonObject.getString("data"));
                    }
                    ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_LAUNCH_VISION_PROGRAM, bundle);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


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

        root.addView(webView);
    }

    public void setTitle(final String title, boolean nvgShow) {
        tvTitle.setText(title);
        if (jsBridge != null) {
            jsBridge.setJsTitle(title);
        }
        rlTitleBar.setVisibility(nvgShow ? VISIBLE : GONE);
    }

    public void fetchTargetUrl(String appletId, String data) {
        this.appletId = appletId;
        loadingContent.setVisibility(VISIBLE);
        webView.setVisibility(GONE);
        startLoadingAnimation();
        controller.startH5Program(appletId);
        freshProgram(appletId);

        if (jsBridge == null) {
            jsBridge = new JsBridge(getContext(), webView, controller.getPlatform());
        }
        jsBridge.setJsData(data);
        webView.setJsBridge(jsBridge);
    }


    public void freshProgram(String appletId) {
        controller.refreshRecentHistory(appletId);
    }

    public void openLink(final String url) {
        VenvyLog.d("openLink : " + url);

        webView.setVisibility(VISIBLE);
        loadingContent.setVisibility(GONE);
        webView.loadUrl(url);
        webView.setTag(url);
        cancelLoadingAnimation();
    }

    public void addDeveloperUserIdToJsBridge(String developerUserId) {
        if (jsBridge != null) {
            jsBridge.setDeveloperUserId(developerUserId);
            webView.setJsBridge(jsBridge);
        }
    }

    public void setWebViewCloseListener(final WebViewCloseListener closeListener) {
        if (jsBridge != null) {
            jsBridge.setWebViewCloseListener(new JsBridge.WebViewCloseListener() {
                @Override
                public void onClose(CloseType actionType) {
                    if (closeListener != null) {
                        closeListener.onClose(appletId);
                    }
                }
            });
        }
    }

    public void reload(String data) {
        if (jsBridge != null) {
            jsBridge.setJsData(data);
        }
        webView.setJsBridge(jsBridge);
        webView.loadUrl(String.valueOf(webView.getTag()));
        freshProgram(appletId);
    }

    public void onScreenChanged(boolean isHorizontal) {
        // 横竖屏切换要call一下js这个方法
        webView.callJsFunction("orientationChange", isHorizontal ? "0" : "1");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (webView != null) {
            removeView(webView);
        }
    }

    public interface WebViewCloseListener {
        void onClose(String appletId);
    }
}
