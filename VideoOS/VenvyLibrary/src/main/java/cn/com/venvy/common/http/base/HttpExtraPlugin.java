package cn.com.venvy.common.http.base;

/**
 * Created by yanjiangbo on 2017/6/9.
 */

public abstract class HttpExtraPlugin {

    private String mResult;

    public abstract boolean isSuccess() throws Exception;

    public void setResponseStringResult(String result) {
        this.mResult = result;
    }

    public String getResponseResult() {
        return mResult;
    }
}
