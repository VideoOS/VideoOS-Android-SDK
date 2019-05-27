package both.video.venvy.com.appdemo.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import both.video.venvy.com.appdemo.R;

/**
 * Created by Lucas on 2019/5/23.
 */
public class VideoIdConfigAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public VideoIdConfigAdapter(List<String> dataSize) {
        super(R.layout.item_config, dataSize);
    }

    @Override
    protected void convert(BaseViewHolder helper, String videoId) {
        helper.setText(R.id.info_config, TextUtils.isEmpty(videoId) ? "" : videoId);
    }
}
