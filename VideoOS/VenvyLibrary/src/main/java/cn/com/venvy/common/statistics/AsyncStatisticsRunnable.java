package cn.com.venvy.common.statistics;

import cn.com.venvy.Platform;

/**
 * Created by videopls on 2019/8/22.
 */

public class AsyncStatisticsRunnable implements Runnable {
    private String dataJson;
    private Platform  platform;

    public AsyncStatisticsRunnable(Platform platform,String dataJson) {
        this.platform = platform;
        this.dataJson = dataJson;
    }

    @Override
    public void run() {
        VideoPlusStatisticsModel videoPlusStatisticsModel = new VideoPlusStatisticsModel(platform, dataJson, new VideoPlusStatisticsModel.VideoPlusStatisticsCallback() {
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
