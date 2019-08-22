package both.video.venvy.com.appdemo.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.adapter.AppKeyConfigAdapter;
import both.video.venvy.com.appdemo.adapter.AppSecretConfigAdapter;
import both.video.venvy.com.appdemo.adapter.VideoIdConfigAdapter;
import both.video.venvy.com.appdemo.adapter.VideoUrlConfigAdapter;
import both.video.venvy.com.appdemo.bean.ConfigBean;
import both.video.venvy.com.appdemo.bean.ConfigData;
import both.video.venvy.com.appdemo.bean.VideoInfoData;
import both.video.venvy.com.appdemo.http.AppConfigModel;
import both.video.venvy.com.appdemo.http.VideoConfigModel;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.VideoType;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.common.utils.VenvyUIUtil;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class VideoOsConfigDialog {
    private AlertDialog mDialog;
    private Context mContext;
    private EditText mPathView, mNativeView, mAppKeyView, mAppSecretView;
    private SettingChangedListener listener;

    private VideoUrlPopup mVideoUrlPopup;
    private VideoIdPopup mVideoIdPopup;
    private AppKeyPopup mAppKeyPopup;
    private AppSecretPopup mAppSecretPopup;

    private AppConfigModel mAppConfigModel;
    private VideoConfigModel mVideoConfigModel;

    public VideoOsConfigDialog(final Context context, VideoType type) {
        mContext = context;
        mDialog = new AlertDialog.Builder(context).create();
        final View osConfigView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_setting_videoos, null, false);
        mPathView = (EditText) osConfigView.findViewById(R.id.sp_setting_app_video_path);
        mPathView.setText(ConfigUtil.getVideoId());

        mPathView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus || mVideoIdPopup == null || mVideoIdPopup.isShowing()) {
                    return;
                }
                mVideoIdPopup.showPopupWindow(mPathView);
            }
        });

        mNativeView = (EditText) osConfigView.findViewById(R.id.sp_setting_app_id);
        mNativeView.setText(ConfigUtil.getVideoName());
        mNativeView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus || mVideoUrlPopup == null  || mVideoUrlPopup.isShowing()){
                    return;
                }
                mVideoUrlPopup.showPopupWindow(mNativeView);
            }
        });




        mAppKeyView = (EditText) osConfigView.findViewById(R.id.sp_setting_app_video_appKey);
        mAppKeyView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus || mAppKeyPopup == null || mAppKeyPopup.isShowing()) {
                    return;
                }
                mAppKeyPopup.showPopupWindow(mAppKeyView);
            }
        });

        mAppSecretView = (EditText) osConfigView.findViewById(R.id.sp_setting_app_video_appSecret);
        mAppSecretView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus || mAppSecretPopup == null || mAppSecretPopup.isShowing()) {
                    return;
                }
                mAppSecretPopup.showPopupWindow(mAppSecretView);
            }
        });

        mAppKeyView.setText(ConfigUtil.getAppKey());
        mAppSecretView.setText(ConfigUtil.getAppSecret());
        initPopup();
        osConfigView.findViewById(R.id.bt_setting_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog == null)
                    return;
                mDialog.dismiss();
            }
        });
        osConfigView.findViewById(R.id.bt_setting_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = mPathView.getText().toString();
                String appKey = mAppKeyView.getText().toString();
                String appSecret = mAppSecretView.getText().toString();
                String videoUrl = mNativeView.getText().toString();
                if (TextUtils.isEmpty(videoId) || TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)) {
                    Toast.makeText(context, "请检查你输入的videoID,appKey,appSecret,参数不可为空", Toast.LENGTH_LONG).show();
                    return;
                }
                ConfigUtil.putAppKey(appKey);
                ConfigUtil.putAppSecret(appSecret);
                ConfigUtil.putVideoId(videoId);
                ConfigUtil.putVideoName(videoUrl);

                ConfigBean bean = new ConfigBean.Builder()
                        .setVideoUrl(videoUrl)
                        .setVideoId(videoId)
                        .setAppKey(appKey)
                        .setAppSecret(appSecret)
                        .build();
                if (listener != null) {
                    listener.onChangeStat(bean);
                }
                mDialog.dismiss();
            }
        });
        mDialog.setView(osConfigView);
    }

    public void showOsSetting() {
        mDialog.show();

        queryAppKey();
        queryVideoInfo();
    }

    /**
     * 获取配置的VideoId
     *
     * @return
     */
    public String getVideoId() {
        return mPathView != null ? mPathView.getText().toString() : null;
    }

    /***
     * 获取配置的素材名称
     * @return
     */
    public String getCreativeName() {
        return mNativeView != null ? mNativeView.getText().toString() : null;
    }

    public String getAppKey() {
        return mAppKeyView != null ? mAppKeyView.getText().toString() : null;
    }

    public String getAppSecret() {
        return mAppSecretView != null ? mAppSecretView.getText().toString() : null;
    }

    public void onChangeListener(SettingChangedListener l) {
        listener = l;
    }

    public interface SettingChangedListener {
        void onChangeStat(ConfigBean bean);
    }


    private void initPopup() {
        mVideoUrlPopup = new VideoUrlPopup(mContext);
        mVideoIdPopup = new VideoIdPopup(mContext);
        mAppKeyPopup = new AppKeyPopup(mContext);
        mAppSecretPopup = new AppSecretPopup(mContext);


        VideoUrlConfigAdapter videoUrlConfigAdapter = mVideoUrlPopup.getAdapter();
        VideoIdConfigAdapter videoIdConfigAdapter = mVideoIdPopup.getAdapter();
        AppKeyConfigAdapter appKeyConfigAdapter = mAppKeyPopup.getAdapter();
        AppSecretConfigAdapter appSecretConfigAdapter = mAppSecretPopup.getAdapter();
        if (appKeyConfigAdapter != null) {
            appKeyConfigAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mAppKeyPopup.dismiss();
                    ConfigBean bean = (ConfigBean) adapter.getData().get(position);
                    mAppKeyView.setText(bean.getAppKey());
                    mAppSecretView.setText(bean.getAppSecret());
                }
            });
        }
        if (appSecretConfigAdapter != null) {
            appSecretConfigAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mAppSecretPopup.dismiss();
                    ConfigBean bean = (ConfigBean) adapter.getData().get(position);
                    mAppKeyView.setText(bean.getAppKey());
                    mAppSecretView.setText(bean.getAppSecret());
                }
            });
        }
        if (videoIdConfigAdapter != null) {
            videoIdConfigAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mVideoIdPopup.dismiss();
                    ConfigBean bean = (ConfigBean) adapter.getData().get(position);
                    mPathView.setText(bean.getVideoId());
                    mNativeView.setText(bean.getVideoUrl());
                }
            });
        }


        if(videoUrlConfigAdapter != null){
            videoUrlConfigAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    mVideoUrlPopup.dismiss();
                    ConfigBean bean = (ConfigBean) adapter.getData().get(position);
                    mPathView.setText(bean.getVideoId());
                    mNativeView.setText(bean.getVideoUrl());
                }
            });
        }

    }


    /**
     * test环境appkey&secret：
     * eed99d68-6f1d-403a-a9f4-0583cccc20e9
     * 941f336cf231470b
     * <p>
     * b1e3521e-6795-4ad6-a6ff-3ab61d019301
     * 634469319d5d46d6
     * <p>
     * c21d0393-5946-4104-b291-6334147cc45d
     * 36b1e5f6d94441f0
     *
     * @return
     */
    private List<ConfigBean> generatePopupData() {
        List<ConfigBean> data = new ArrayList<>();
        if (DebugStatus.isRelease()) {// 正式环境
            data.add(new ConfigBean(ConfigUtil.RELEASE_APP_KEY, ConfigUtil.RELEASE_APP_SECRET));
        } else if (DebugStatus.isPreView()) {// 测试环境
            data.add(new ConfigBean("eed99d68-6f1d-403a-a9f4-0583cccc20e9", "941f336cf231470b"));
            data.add(new ConfigBean("b1e3521e-6795-4ad6-a6ff-3ab61d019301", "634469319d5d46d6"));
            data.add(new ConfigBean("c21d0393-5946-4104-b291-6334147cc45d", "36b1e5f6d94441f0"));
            data.add(new ConfigBean("3ffc4acd-ed37-488c-b20e-6a939beccc95", "83df4594e0794422"));
        }
        return data;
    }

    private List<ConfigBean> generateVideoIdData() {
        List<ConfigBean> data = new ArrayList<>();
        data.add(new ConfigBean.Builder().setVideoId("http://qa-video.oss-cn-beijing.aliyuncs.com/ai/buRan.mp4").build());
        data.add(new ConfigBean.Builder().setVideoId("http://qa-video.oss-cn-beijing.aliyuncs.com/mp4/zongyi.mp4").build());
        data.add(new ConfigBean.Builder().setVideoId("http://qa-video.oss-cn-beijing.aliyuncs.com/mp4/mby02.mp4").build());
        data.add(new ConfigBean.Builder().setVideoId("http://qa-video.oss-cn-beijing.aliyuncs.com/mp4/shn48.mp4").build());
        data.add(new ConfigBean.Builder().setVideoId("25").build());
        data.add(new ConfigBean.Builder().setVideoId("40").build());
        return data;
    }

    protected void queryAppKey() {
        if (mAppConfigModel == null) {
            mAppConfigModel = new AppConfigModel(new AppConfigModel.AppConfigCallback() {
                @Override
                public void updateComplete(final String result) {
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            ConfigData configData = JSON.parseObject(result, ConfigData.class);
                            if (configData == null) {
                                return;
                            }
                            ConfigData.ConfigInfo configInfo = configData.getData();
                            if (configInfo == null) {
                                return;
                            }
                            List<ConfigBean> data = new ArrayList<>();
                            for (ConfigData.ConfigInfo.AppInfo info : configInfo.getApps()) {
                                data.add(new ConfigBean(info.getAppKey(), info.getAppSecret()));
                            }
                            mAppKeyPopup.addData(data);
                            mAppSecretPopup.addData(data);


                        }
                    });

                }

                @Override
                public void updateError(Throwable t) {
                    List<ConfigBean> data = generatePopupData();
                    mAppKeyPopup.addData(data);
                    mAppSecretPopup.addData(data);
                }
            });
        }
        mAppConfigModel.startRequest();
    }


    protected void queryVideoInfo() {
        if (mVideoConfigModel == null) {
            mVideoConfigModel = new VideoConfigModel(new VideoConfigModel.VideoConfigCallback() {
                @Override
                public void requestComplete(final String result) {
                    VenvyUIUtil.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            VideoInfoData videoData = JSON.parseObject(result, VideoInfoData.class);
                            if (videoData == null) {
                                return;
                            }
                            VideoInfoData.VideoData video = videoData.getData();
                            if (video == null) {
                                return;
                            }
                            List<ConfigBean> data = new ArrayList<>();
                            for (VideoInfoData.VideoData.VideoInfo info : video.getVideos()) {
                                data.add(new ConfigBean.Builder()
                                        .setVideoId(info.getVideoId())
                                        .setVideoUrl(info.getVideoUrl())
                                        .build());
                            }
                            mVideoIdPopup.addData(data);
                            mVideoUrlPopup.addData(data);
                        }
                    });

                }

                @Override
                public void requestError(Throwable t) {
                    VenvyLog.e("video requestError : " + t.getMessage());
                    mVideoIdPopup.addData(generateVideoIdData());
                }
            });
        }
        mVideoConfigModel.startRequest();
    }


}
