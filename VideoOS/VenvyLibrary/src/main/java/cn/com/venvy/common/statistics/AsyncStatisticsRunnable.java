package cn.com.venvy.common.statistics;

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
