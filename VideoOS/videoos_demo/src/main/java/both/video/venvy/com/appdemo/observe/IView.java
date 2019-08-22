package both.video.venvy.com.appdemo.observe;

import android.support.annotation.StringRes;

public interface IView {
    void showLoading();

    void hideLoading();

    void onError(String message);

    void onError(@StringRes int resId);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    void hideKeyboard();

    boolean isNetworkConnected();
}
