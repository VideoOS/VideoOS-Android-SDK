package cn.com.venvy.common.interf;

/**
 * Created by Lucas on 2019/11/7.
 */
public interface IAppletListener {
    boolean canGoBack();
    void goBack();
    void closeView();
}
