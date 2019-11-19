package both.video.venvy.com.appdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.taobao.luaview.util.TextUtil;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.utils.ConfigUtil;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText appKeyView;
    private EditText appSecretView;

    public static void newIntent(Context context){
        context.startActivity(new Intent(context,ConfigActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
    }

    private void initView() {
        appKeyView    = this.findViewById(R.id.config_ed_AppKey);
        appSecretView = this.findViewById(R.id.config_ed_AppSecret);
        this.findViewById(R.id.config_back).setOnClickListener(this);
        this.findViewById(R.id.config_update).setOnClickListener(this);

        if(!TextUtils.isEmpty(ConfigUtil.getAppKey()) && !TextUtils.isEmpty(ConfigUtil.getAppSecret())){
            appKeyView.setText(ConfigUtil.getAppKey());
            appSecretView.setText(ConfigUtil.getAppSecret());
        }
//        appKeyView.setText("dc8b6c9b-a592-4100-a532-9886ffce7dce");
//        appSecretView.setText("0b511c09045c4876");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.config_back:
                hintKeyBoard();
                finish();
                break;
            case R.id.config_update:
                updateConfig();
                break;
        }
    }

    private void updateConfig() {
        String appKey = appKeyView.getText().toString().trim();
        String appSecret = appSecretView.getText().toString().trim();
        if(TextUtils.isEmpty(appKey) || TextUtils.isEmpty(appSecret)){
            Toast.makeText(this, "appKey/appSecret必须填写。", Toast.LENGTH_SHORT).show();
            return;
        }
        ConfigUtil.putAppKey(appKey);
        ConfigUtil.putAppSecret(appSecret);

        if(ConfigUtil.getAppKey().equals(appKey) && ConfigUtil.getAppSecret().equals(appSecret)){
            hintKeyBoard();
            finish();
        }else{
            Toast.makeText(this, "appKey/appSecret更新失败。", Toast.LENGTH_SHORT).show();
        }
    }


    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if(imm.isActive()&&getCurrentFocus()!=null){
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken()!=null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
