package both.video.venvy.com.appdemo.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.luaj.vm2.Lua;
import org.luaj.vm2.ast.Str;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;
import cn.com.venvy.Config;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.VideoType;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class VideoOsConfigDialog implements RadioGroup.OnCheckedChangeListener {
    private AlertDialog mDialog;
    private Context mContext;
    private EditText mPathView, mNativeView;
    private SettingChangedListener listener;

    public VideoOsConfigDialog(Context context, VideoType type) {
        mContext = context;
        mDialog = new AlertDialog.Builder(context).create();
        final View osConfigView = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_setting_videoos, null, false);
        mPathView = (EditText) osConfigView.findViewById(R.id.sp_setting_app_video_path);
        if (type == VideoType.LIVEOS) {
            mPathView.setText("25");
        }
        mNativeView = (EditText) osConfigView.findViewById(R.id.sp_setting_creative_name_id);
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
                if (listener == null)
                    return;
                ConfigBean bean = new ConfigBean();
                bean.setCreativeName(mNativeView.getText().toString());
                bean.setVideoId(mPathView.getText().toString());
                listener.onChangeStat(bean);
                mDialog.dismiss();
            }
        });
        RadioGroup radioGroup = (RadioGroup) osConfigView.findViewById(R.id.layout_setting_change_environment);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_debug) {
                    DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.DEBUG);
                } else if (checkedId == R.id.rb_preview) {
                    DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
                } else {
                    DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.RELEASE);
                }
            }
        });
        mDialog.setView(osConfigView);
    }

    public void showOsSetting() {

        mDialog.show();
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

    public void onChangeListener(SettingChangedListener l) {
        listener = l;
    }

    public interface SettingChangedListener {
        void onChangeStat(ConfigBean bean);
    }

    //环境切换
    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.rb_debug) {
            DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.DEBUG);
        } else if (checkedId == R.id.rb_preview) {
            DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
        } else if (checkedId == R.id.rb_release) {
            DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.RELEASE);
        }
    }
}
