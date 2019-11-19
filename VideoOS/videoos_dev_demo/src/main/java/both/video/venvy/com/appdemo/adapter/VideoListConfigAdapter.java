package both.video.venvy.com.appdemo.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.VideoInfo;

/**
 * Created by videopls on 2019/10/12.
 */

public class VideoListConfigAdapter extends BaseQuickAdapter<VideoInfo, BaseViewHolder> {
    public VideoListConfigAdapter(List<VideoInfo> dataSize) {
        super(R.layout.item_config, dataSize);
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoInfo item) {
        String videoData = item.videoData;
        helper.setText(R.id.info_config, TextUtils.isEmpty(videoData) ? "" : videoData);
    }
}