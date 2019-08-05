package cn.com.videopls.pub;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.luaview.util.JsonUtil;
import com.taobao.luaview.util.TextUtil;

import java.util.HashMap;

import cn.com.venvy.common.observer.ObservableManager;
import cn.com.venvy.common.observer.VenvyObservable;
import cn.com.venvy.common.observer.VenvyObservableTarget;
import cn.com.venvy.common.observer.VenvyObserver;
import cn.com.venvy.common.router.IRouterCallback;

/**
 * Created by Lucas on 2019/7/31.
 */
public class VideoProgramToolBarView extends LinearLayout implements VenvyObserver {


    protected VideoProgramView videoProgramView;
    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView ivClose;

    private String currentAppletId;

    public VideoProgramToolBarView(@NonNull Context context) {
        super(context);
        init();
    }

    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public VideoProgramToolBarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ObservableManager.getDefaultObserable().addObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM, this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ObservableManager.getDefaultObserable().removeObserver(VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM, this);
    }


    private void init() {
        inflate(getContext(), R.layout.video_program_tool, this);
        videoProgramView = (VideoProgramView) findViewById(R.id.programView);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivClose = (ImageView) findViewById(R.id.ivClose);
        ivBack.setVisibility(INVISIBLE);
        ivBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                videoProgramView.removeTopView();
                Toast.makeText(getContext(), "back click : "+videoProgramView.getAllOfLuaView(), Toast.LENGTH_SHORT).show();
                ivBack.setVisibility(videoProgramView.getAllOfLuaView() > 1 ? VISIBLE : INVISIBLE);
            }
        });
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(currentAppletId)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(VenvyObservableTarget.KEY_APPLETS_ID, currentAppletId);
                    ObservableManager.getDefaultObserable().sendToTarget(VenvyObservableTarget.TAG_CLOSE_VISION_PROGRAM, bundle);
                }
                Toast.makeText(getContext(), "ivClose click", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void start(String appletId, String data, int type) {
        this.currentAppletId = appletId;
        videoProgramView.startVision(appletId, data, type);
    }

    public void navigation(Uri uri, HashMap<String, String> params, IRouterCallback callback) {
        videoProgramView.navigation(uri, params, callback);
    }

    public void setVideoOSAdapter(@NonNull VideoPlusAdapter adapter) {
        videoProgramView.setVideoOSAdapter(adapter);
    }

    @Override
    public void notifyChanged(VenvyObservable observable, String tag, Bundle bundle) {
        if (VenvyObservableTarget.TAG_ADD_LUA_SCRIPT_TO_VISION_PROGRAM.equals(tag)) {
            ivBack.setVisibility(videoProgramView.getAllOfLuaView() > 1 ? VISIBLE : INVISIBLE);
        }
    }
}
