package both.video.venvy.com.appdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import both.video.venvy.com.appdemo.R;

/**
 * Created by videojj_pls on 2018/11/7.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String USER_INFO_ERROR_HINT = "请检查你的账号或密码";
    private static final String USER_NAME = "userName";
    private static final String USER_PWD = "userPwd";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText edName = (EditText) findViewById(R.id.ed_name);
        final EditText edPwd = (EditText) findViewById(R.id.ed_pwd);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = edName.getText().toString();
                String userPwd = edPwd.getText().toString();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(userPwd)) {
                    Toast.makeText(LoginActivity.this, USER_INFO_ERROR_HINT, Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = getIntent();
                intent.putExtra(USER_NAME, userName);
                intent.putExtra(USER_PWD, userPwd);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
