package cn.com.venvy.common.statistics;

import java.util.List;

/**
 * Created by videopls on 2019/8/22.
 */

public class StatisticsInfoBean {
    //预加载流量统计
    public String videoId;
    public int downLoadStage;
    public List<FileInfoBean> fileInfoBeans;

    public static class FileInfoBean {
        public String fileName;
        public String filePath;
        public long fileSize;
    }
}
