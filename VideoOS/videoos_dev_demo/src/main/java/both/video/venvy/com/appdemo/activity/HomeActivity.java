package both.video.venvy.com.appdemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ConfigUtil;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.home_config).setOnClickListener(this);
        this.findViewById(R.id.home_interact_program).setOnClickListener(this);
        this.findViewById(R.id.home_service_program).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_config:
                ConfigActivity.newIntent(this);
                break;
            case R.id.home_interact_program:
                if(isCachedAppKeySecret()){
                    InteractActivity.newIntent(this);
                }else{
                    Toast.makeText(HomeActivity.this,"请先填写后台的应用信息再调试",Toast.LENGTH_SHORT).show();
                    ConfigActivity.newIntent(HomeActivity.this);
                }
                break;
            case R.id.home_service_program:
                if (isCachedAppKeySecret()) {
                    ServiceActivity.newIntent(this);
                }else{
                    Toast.makeText(HomeActivity.this,"请先填写后台的应用信息再调试",Toast.LENGTH_SHORT).show();
                    ConfigActivity.newIntent(HomeActivity.this);
                }
                break;
        }
    }

    public boolean isCachedAppKeySecret() {
        if(!TextUtils.isEmpty(ConfigUtil.getAppKey()) && !TextUtils.isEmpty(ConfigUtil.getAppSecret())){
            return true;
        }
        return false;
    }
}
