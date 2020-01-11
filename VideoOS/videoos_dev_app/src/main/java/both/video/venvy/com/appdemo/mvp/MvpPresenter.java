package both.video.venvy.com.appdemo.mvp;
/**
 * Created by zhangjunling on 18-5-11.
 */

//抽象类->高度抽象
public class MvpPresenter<V extends MvpView> {


    //持有V层引用
    private V view;


    public V getView() {
        return view;
    }

    //绑定UI层
    public void attachView(V view){
        this.view = view;
    }

    //解除绑定
    public void detachView(){
        this.view = null;
        destroy();
    }

    protected void destroy(){

    }
}
