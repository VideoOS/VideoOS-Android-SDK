
package cn.com.venvy.lua.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.widget.RelativeLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.DimenUtil;
import com.taobao.luaview.util.LuaViewUtil;
import com.taobao.luaview.view.drawable.LVGradientDrawable;
import com.taobao.luaview.view.foreground.ForegroundRelativeLayout;
import com.taobao.luaview.view.interfaces.ILVViewGroup;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;

import cn.com.venvy.common.media.view.CustomVideoView;
import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.lua.ud.VenvyUDMediaPlayerView;


/**
 * Created by lgf on 2018/1/19.
 */

public class VenvyMediaPlayerView extends ForegroundRelativeLayout implements ILVViewGroup {
    private VenvyUDMediaPlayerView mLuaUserdata;
    private CustomVideoView customVideoView;
    LVGradientDrawable mStyleDrawable = null;
    Path mPath = null;

    public VenvyMediaPlayerView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        mLuaUserdata = new VenvyUDMediaPlayerView(this, globals, metaTable, varargs != null ? varargs.arg1() : null);
        init(getContext());
    }

    private void init(Context context) {
        customVideoView = new CustomVideoView(context);
        this.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        this.addView(customVideoView, params);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_MEDIA_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED, mLuaUserdata);
    }

    @Override
    protected void onDetachedFromWindow() {
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_CLIP_MEDIA_STATUS_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_MEDIA_CHANGED, mLuaUserdata);
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_VOLUME_STATUS_CHANGED, mLuaUserdata);
        super.onDetachedFromWindow();
    }

    public CustomVideoView getCustomVideoView() {
        return customVideoView;
    }


    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

    @Override
    public void setChildNodeViews(ArrayList<UDView> childNodeViews) {
        mLuaUserdata.setChildNodeViews(childNodeViews);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean hasStyle = setupStyleDrawable();

        if (hasStyle && canvas != null) {
            try {
                canvas.clipPath(getClipPath());
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            }
        }

        super.onDraw(canvas);

        if (hasStyle) {//背景放到上面画，默认为透明颜色
            mStyleDrawable.setColor(Color.TRANSPARENT);
            mStyleDrawable.draw(canvas);
        }
    }

    /**
     * get clip path of StyleDrawable
     *
     * @return
     */
    Path getClipPath() {
//        if (mPath == null) {
//            mPath = new Path();
//        }
        mPath = new Path();
        Rect rect = mStyleDrawable.getBounds();
        float radius = mStyleDrawable.getCornerRadius();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPath.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius, radius, Path.Direction.CW);
        } else {
            mPath.addCircle(rect.left + radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.top + radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.right - radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addCircle(rect.left + radius, rect.bottom - radius, radius, Path.Direction.CW);
            mPath.addRect(rect.left + radius, rect.top, rect.right - radius, rect.bottom, Path.Direction.CW);
            mPath.addRect(rect.left, rect.top + radius, rect.right, rect.bottom - radius, Path.Direction.CW);
        }
        return mPath;
    }

    /**
     * 设置好drawable的样式
     *
     * @return
     */
    boolean setupStyleDrawable() {
        if (mStyleDrawable != null) {
            mStyleDrawable.setBounds(0, 0, getWidth(), getHeight());
            return true;
        }
        return false;
    }

    synchronized LVGradientDrawable getStyleDrawable() {
        if (mStyleDrawable == null) {
            mStyleDrawable = new LVGradientDrawable();
        }
        return mStyleDrawable;
    }

    /**
     * set corner radius
     *
     * @param radius
     */
    public void setCornerRadius(float radius) {
        getStyleDrawable().setCornerRadius(radius);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setCornerRadius(DimenUtil.dpiToPx(radius));
        LuaViewUtil.setBackground(this, drawable);
    }

    public float getCornerRadius() {
        if (mStyleDrawable != null) {
            return mStyleDrawable.getCornerRadius();
        }
        return 0;
    }

    /**
     * 设置边框宽度
     */
    public void setStrokeWidth(int width) {
        getStyleDrawable().setStrokeWidth(width);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setStrokeWidth(width);
        LuaViewUtil.setBackground(this, drawable);
    }

    public int getStrokeWidth() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeWidth() : 0;
    }

    /**
     * 设置边框颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        getStyleDrawable().setStrokeColor(color);
        LVGradientDrawable drawable = this.getBackground() instanceof LVGradientDrawable ? (LVGradientDrawable) this.getBackground() : new LVGradientDrawable();
        drawable.setStrokeColor(color);
        LuaViewUtil.setBackground(this, drawable);
    }

    public int getStrokeColor() {
        return mStyleDrawable != null ? mStyleDrawable.getStrokeColor() : 0;
    }
}
