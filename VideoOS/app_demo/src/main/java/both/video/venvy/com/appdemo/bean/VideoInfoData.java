package both.video.venvy.com.appdemo.bean;

import java.util.List;

/**
 * Created by Lucas on 2019/8/6.
 */
public class VideoInfoData {

    private VideoData data;

    public VideoData getData() {
        return data;
    }

    public void setData(VideoData data) {
        this.data = data;
    }

    public static class VideoData {
        private List<VideoInfo> infoList;


        public List<VideoInfo> getInfoList() {
            return infoList;
        }

        public void setInfoList(List<VideoInfo> infoList) {
            this.infoList = infoList;
        }

        public static class VideoInfo {
            /**
             * videoUrl : http://ai-video.oss-cn-beijing-internal.aliyuncs.com/576ca415ca438532011b3c70/%e4%b8%83%e6%9c%88%e4%b8%8e%e5%ae%89%e7%94%9f03.mp4
             * videoId : 5d3eb40f41613e9668fd730e
             */

            private String videoUrl;
            private String videoId;

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }
        }
    }
}
