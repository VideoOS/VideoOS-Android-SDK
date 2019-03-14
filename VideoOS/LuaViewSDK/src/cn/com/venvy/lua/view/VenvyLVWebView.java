package cn.com.venvy.lua.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.view.interfaces.ILVNativeViewProvider;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import cn.com.venvy.common.webview.IVenvyWebView;
import cn.com.venvy.common.webview.IVenvyWebViewClient;
import cn.com.venvy.common.webview.WebViewFactory;
import cn.com.venvy.lua.ud.VenvyUDWebView;


/**
 * Created by Arthur on 2017/9/4.
 */

public class VenvyLVWebView extends FrameLayout implements ILVNativeViewProvider, ILVView {

    protected UDView mLuaUserdata;
    protected IVenvyWebView mWebView;
    protected boolean mIsLoading;

    public VenvyLVWebView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDWebView(this, globals, metaTable, varargs);
    }

    public void init(Context context) {
        this.mWebView = WebViewFactory.createWebView(context);
        if (mWebView instanceof View) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((View) mWebView).setLayoutParams(params);
            addView((View) mWebView);
        }
        mWebView.setWebViewClient(new IVenvyWebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(View webView, String url) {
                return false;
            }

            @Override
            public void onPageStarted(View webView, String url, Bitmap favicon) {

            }

            @Override
            public void onPageFinished(View webView, String url) {

            }

            @Override
            public void onReceivedError(View webView, int errorCode, String description, String failingUrl) {

            }
        });

        this.setEnabled(true);
    }

    public IVenvyWebView getWebView() {
        return mWebView;
    }

    public boolean getLoadingState() {
        return mIsLoading;
    }


    @Override
    public View getNativeView() {
        if (mWebView instanceof View) {
            return (View) this.getWebView();
        }

        return null;
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }
}
