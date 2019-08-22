package both.video.venvy.com.appdemo;

import java.util.List;

import both.video.venvy.com.appdemo.bean.ConfigData;
import both.video.venvy.com.appdemo.observe.IView;

public interface IConfigView extends IView {
    void doBackClick();//返回按钮点击

    void doUpdate(String appKey, String appSecret);//更新配置点击

    void doNetConfigInfo(List<ConfigData.ConfigInfo.AppInfo> appInfo);
}
