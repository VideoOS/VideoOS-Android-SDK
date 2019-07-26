package cn.com.videopls.pub;

/**
 * Created by yanjiangbo on 2018/4/12.
 */

public interface IStartQueryResult {
    void successful(String result, ServiceQueryAdsInfo queryAdsInfo);

    void failed(Throwable throwable);
}
