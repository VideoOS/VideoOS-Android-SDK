package cn.com.venvy.common.statistics;

import android.util.Log;
import cn.com.venvy.Platform;

/**
 * Created by videopls on 2019/8/22.
 */

public class AsyncStatisticsRunnable implements Runnable {
    private StatisticsInfoBean statisticsInfoBean;
    private Platform platform;

    public AsyncStatisticsRunnable(Platform platform,StatisticsInfoBean statisticsInfoBean) {
        this.platform = platform;
        this.statisticsInfoBean = statisticsInfoBean;
    }

    @Override
    public void run() {
        Log.i("zhangjunling", "run()");

        Log.i("zhangjunling", "size:" + statisticsInfoBean.fileInfoBeans.size());
//        Log.i("zhangjunling", "data:" + statisticsInfoBean.fileInfoBeans.toString());

//        if(true){
//            return;
//        }

        VideoPlusStatisticsModel videoPlusStatisticsModel = new VideoPlusStatisticsModel(platform, statisticsInfoBean, new VideoPlusStatisticsModel.VideoPlusStatisticsCallback() {
            @Override
            public void updateComplete(String result) {

            }

            @Override
            public void updateError(Throwable t) {

            }
        });
        videoPlusStatisticsModel.startRequest();
    }

}
