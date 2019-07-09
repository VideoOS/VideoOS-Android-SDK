package both.video.venvy.com.appdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;
import both.video.venvy.com.appdemo.utils.ConfigUtil;
import both.video.venvy.com.appdemo.widget.VideoOsConfigDialog;
import cn.com.venvy.common.debug.DebugStatus;
import cn.com.venvy.common.interf.VideoType;

/**
 * Created by videojj_pls on 2018/9/13.
 */

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_app_key;
    TextView tv_app_secret;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_home_item_1).setOnClickListener(this);
        findViewById(R.id.tv_home_item_2).setOnClickListener(this);
        tv_app_key = findViewById(R.id.tv_app_key);
        tv_app_secret = findViewById(R.id.tv_app_secret);

        ((RadioButton) findViewById(R.id.rb_release)).setChecked(DebugStatus.isRelease());
        ((RadioButton) findViewById(R.id.rb_debug)).setChecked(DebugStatus.isDebug());
        ((RadioButton) findViewById(R.id.rb_preview)).setChecked(DebugStatus.isPreView());

        ((RadioGroup) findViewById(R.id.rg_environment)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_release:
                        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.RELEASE);
                        showConfigDialog();
                        break;
                    case R.id.rb_debug:
                        ConfigUtil.putAppKey(ConfigUtil.DEV_APP_KEY);
                        ConfigUtil.putAppSecret(ConfigUtil.DEV_APP_SECRET);
                        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.DEBUG);
                        tv_app_key.setText(getString(R.string.current_app_key, ConfigUtil.getAppKey()));
                        tv_app_secret.setText(getString(R.string.current_app_secret, ConfigUtil.getAppSecret()));
                        break;
                    case R.id.rb_preview:
                        DebugStatus.changeEnvironmentStatus(DebugStatus.EnvironmentStatus.PREVIEW);
                        showConfigDialog();
                        break;
                }
            }
        });

    }


    private void showConfigDialog(){
        VideoOsConfigDialog dialog = new VideoOsConfigDialog(this, VideoType.VIDEOOS);
        dialog.onChangeListener(new VideoOsConfigDialog.SettingChangedListener(){

            @Override
            public void onChangeStat(ConfigBean bean) {
                tv_app_key.setText(getString(R.string.current_app_key, ConfigUtil.getAppKey()));
                tv_app_secret.setText(getString(R.string.current_app_secret, ConfigUtil.getAppSecret()));
            }
        });
        dialog.showOsSetting();
    }


    @Override
    protected void onResume() {
        super.onResume();
        tv_app_key.setText(getString(R.string.current_app_key, ConfigUtil.getAppKey()));
        tv_app_secret.setText(getString(R.string.current_app_secret, ConfigUtil.getAppSecret()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_home_item_2) {
            startOsActivity();
        } else if (id == R.id.tv_home_item_1) {
            startLiveOsActivity();
        }
    }

    /***
     * 启动OS
     */
    private void startOsActivity() {
        Intent i = new Intent(NavigationActivity.this, OsActivity.class);
        startActivity(i);
    }

    private void startLiveOsActivity() {
        Intent i = new Intent(NavigationActivity.this, LiveActivity.class);
        startActivity(i);
    }
}
