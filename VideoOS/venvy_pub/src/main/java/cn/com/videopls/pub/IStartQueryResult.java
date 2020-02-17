package cn.com.videopls.pub;

/**
 * Created by yanjiangbo on 2018/4/12.
 */

public interface IStartQueryResult {
    void successful(Object result,String miniAppInfo, String originData, ServiceQueryAdsInfo queryAdsInfo);

    void failed(Throwable throwable);
}
