package both.video.venvy.com.appdemo.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;
import both.video.venvy.com.appdemo.utils.AssetsUtil;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.VideoOsConfigDialog;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.router.IRouterCallback;
import cn.com.videopls.pub.Provider;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class OsActivity extends BasePlayerActivity implements View.OnClickListener {

    private VideoOsConfigDialog mConfigDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View settingView = getSettingView();
        mRootView.addView(settingView);
        mConfigDialog = new VideoOsConfigDialog(this, VideoType.VIDEOOS);
        mConfigDialog.onChangeListener(new VideoOsConfigDialog.SettingChangedListener() {
            @Override
            public void onChangeStat(ConfigBean bean) {
                if (mAdapter == null || mVideoPlusView == null)
                    return;
                if (bean == null) {
                    Toast.makeText(OsActivity.this, "配置错误，请确认你输入的配置信息", Toast.LENGTH_LONG).show();
                    return;
                }
                String videoId = bean.getVideoId();
                String appKey = bean.getAppKey();
                String appSecret = bean.getAppSecret();
                String creativeName = bean.getCreativeName();
                tvVideoId.setText(videoId);
                //正在播放视频需要切集操作调用逻辑 没有必须重新创建VideoPlusView 以及VideoPlusAdapter
                mVideoPlusView.stop();
                mVideoPlayer.setUp(videoId, true, ConfigUtil.getVideoName());
                mVideoPlayer.setPlayTag(videoId);
                mVideoPlayer.startPlayLogic();
                mAdapter.updateProvider(changeProvider(videoId, appKey, appSecret, creativeName));
                mVideoPlusView.start();
            }
        });

        mVideoPlayer.setUp(ConfigUtil.getVideoId(), true, ConfigUtil.getVideoName());
        mVideoPlayer.setPlayTag(ConfigUtil.getVideoId());
        mVideoPlayer.startPlayLogic();

    }

    @Override
    protected boolean isLiveOS() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int ID = v.getId();
        if (R.id.iv_os_setting == ID) {
            mConfigDialog.showOsSetting();
        } else if (R.id.bt_os_setting_mall == ID) {
            if (mVideoPlusView == null)
                return;
            mVideoPlusView.stop();
            Uri uri = Uri.parse("LuaView://defaultLuaView?template=os_red_envelope_hotspot.lua&id=os_red_envelope_hotspot");
            HashMap<String, String> params = new HashMap<>();
            params.put("data", AssetsUtil.readFileAssets("local_red.json", OsActivity.this));
            mVideoPlusView.navigation(uri, params, new IRouterCallback() {
                @Override
                public void arrived() {
                    mVideoPlusView.start();
                }

                @Override
                public void lost() {

                }
            });
        } else if (R.id.bt_os_setting_close_window == ID) {
            if (mVideoPlusView == null)
                return;
            mVideoPlusView.closeInfoView();
        }

    }

    /***
     * 底部设置控件
     */
    private View getSettingView() {
        ConstraintLayout mSettingView = null;
        if (mSettingView == null) {
            mSettingView = (ConstraintLayout) LayoutInflater.from(this)
                    .inflate(R.layout.layout_os_setting_button, mRootView, false);
            mSettingView.findViewById(R.id.iv_os_setting).setOnClickListener(this);
            mSettingView.findViewById(R.id.bt_os_setting_mall).setOnClickListener(this);
            mSettingView.findViewById(R.id.bt_os_setting_close_window).setOnClickListener(this);
        }
        return mSettingView;
    }

    private Provider changeProvider(String videoId, String appKey, String appSecret, String creativeName) {
        Provider provider;
        if (TextUtils.isEmpty(creativeName)) {

            provider = new Provider.Builder().setVideoType(VideoType.VIDEOOS).setAppKey(appKey).setAppSecret(appSecret).setCustomUDID(String.valueOf(System.currentTimeMillis()))
                    .setVideoID(videoId)//视频地址
                    .build();
        } else {
            Map<String, String> extendParams = new HashMap<>();
            extendParams.put(TAG_CREATIVE_NAME, creativeName);
            provider = new Provider.Builder().setVideoType(VideoType.VIDEOOS).setAppKey(appKey).setAppSecret(appSecret).setCustomUDID(String.valueOf(System.currentTimeMillis()))
                    .setVideoID(videoId)//视频地址
                    .setExtendJSONString(new JSONObject(extendParams).toString()).build();
        }
        return provider;
    }

}
