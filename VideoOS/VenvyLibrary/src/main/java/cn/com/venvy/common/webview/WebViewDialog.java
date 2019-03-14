package cn.com.venvy.common.webview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;


import cn.com.venvy.common.utils.VenvyResourceUtil;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by lgf on 2017/4/1.
 * 外链显示的对话框
 */

public class WebViewDialog extends Dialog {
    //WebView控件
    private IVenvyWebView mWebView;
    private FrameLayout mParent;

    /***
     * 构造器
     * @param context  上下文
     * @param theme    主题
     */
    public WebViewDialog(Context context, int theme) {
        super(context, theme);
        initView(getContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏大小
        WindowManager.LayoutParams mParams = getWindow().getAttributes();
        //设置宽
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置宽
        mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        //
        getWindow().setAttributes(mParams);
    }

    /***
     * 关闭执行方法
     */
    @Override
    public void dismiss() {
        if (!this.isShowing() && mWebView != null && mParent != null) {
            mParent.post(new Runnable() {
                @Override
                public void run() {
                    mWebView.setWebChromeClient(null);
                    mParent.removeAllViews();
                    mWebView.destroy();
                }
            });
        }
        super.dismiss();
    }

    /***
     * 创建对话框
     * @param context
     * @return
     */
    public static synchronized WebViewDialog getInstance(Context context) {
        WebViewDialog instance = new WebViewDialog(context,
                VenvyResourceUtil.getStyleId(context,
                        "venvy_library_dialog_dg_style"));
        return instance;
    }

    /***
     * 初始化需要的控件
     * @param context
     */
    @JavascriptInterface
    private void initView(final Context context) {
        //父控件
        mParent = new FrameLayout(context);
        //设置背景
        mParent.setBackgroundColor(Color.parseColor("#F0F0F0"));
        //大小参数
        FrameLayout.LayoutParams mParentParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);
        //设置参数
        mParent.setLayoutParams(mParentParams);
        //添加子控件
        FrameLayout mTopLayout = new FrameLayout(context);
        //设置背景
        mTopLayout.setBackgroundColor(Color.parseColor("#2b2b2b"));
        int mTopHight = VenvyUIUtil.dip2px(context, 45);
        //大小参数
        FrameLayout.LayoutParams mTopParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mTopHight);
        //TOP区域添加返回按钮
        ImageButton mBackView = new ImageButton(context);
        //设置点击事件
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //设置本地图片
        mBackView.setBackgroundResource(VenvyResourceUtil.getDrawableOrmipmapId(
                context, "venvy_os_outside_link_back"));
        //
        mBackView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        //大小参数
        FrameLayout.LayoutParams mBackParams = new FrameLayout.LayoutParams(mTopHight, mTopHight);
        mTopLayout.addView(mBackView, mBackParams);
        //进度条区域
        final ProgressBar mProBarView = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        //进度清空
        mProBarView.setProgress(0);
        //设置加载进度的颜色
        mProBarView.setProgressDrawable(context.getResources().getDrawable(
                VenvyResourceUtil.getDrawableId(context,
                        "venvy_library_webview_load_bg")));// 设置
        //大小参数
        FrameLayout.LayoutParams mBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, VenvyUIUtil.dip2px(context, 3));
        //顶部位置
        mBarParams.topMargin = mTopHight;
        //创建WebView控件
        mWebView = WebViewFactory.createWebView(context);
        //大小参数
        final FrameLayout.LayoutParams mWebParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        //距离顶部位置
        mWebParams.topMargin = mTopHight;
        //加载监听
        mWebView.setWebChromeClient(new IVenvyWebChromeClient() {
            @Override
            public void onProgressChanged(View view, int newProgress) {
                if (newProgress >= 100) {
                    mParent.removeView(mProBarView);
                } else {
                    mProBarView.setProgress(newProgress);
                }
            }
        });

        mParent.addView(mTopLayout, mTopParams);
        if (mWebView instanceof View) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((View) mWebView).setLayoutParams(params);
            mParent.addView((View) mWebView);
        }
        //添加进度条
        mParent.addView(mProBarView, mBarParams);
        //加载布局
        setContentView(mParent);
    }


    /***
     * 加载Url
     * @param url
     */
    public WebViewDialog loadUrl(String url) {
        if (mWebView != null && !TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
        return this;
    }
}