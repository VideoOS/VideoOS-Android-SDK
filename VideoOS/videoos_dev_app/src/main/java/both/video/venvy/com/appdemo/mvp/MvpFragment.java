package both.video.venvy.com.appdemo.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by zhangjunling on 18-5-11.
 */

//父类->基类->动态指定类型->泛型设计(通过泛型指定动态类型->由子类指定，父类只需要规定类型范围即可)
public abstract class MvpFragment<V extends MvpView, P extends MvpPresenter<V>> extends Fragment {

    //引用V层和P层
    //有写死了
    private P presenter;

    private V view;

    public P getPresenter() {
        return presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.presenter == null){
            this.presenter = createPresenter();
        }
        if (this.view == null){
            this.view = createView();
        }
        if (this.presenter != null && this.view != null){
            this.presenter.attachView(this.view);
        }
    }

    //由子类指定具体类型
    public abstract P createPresenter();
    public abstract V createView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (this.presenter != null){
            this.presenter.detachView();
        }
    }
}
