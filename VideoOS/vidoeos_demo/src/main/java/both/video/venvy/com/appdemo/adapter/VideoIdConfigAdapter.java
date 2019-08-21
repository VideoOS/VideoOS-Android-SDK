package both.video.venvy.com.appdemo.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;

/**
 * Created by Lucas on 2019/5/23.
 */
public class VideoIdConfigAdapter extends BaseQuickAdapter<ConfigBean, BaseViewHolder> {
    public VideoIdConfigAdapter(List<ConfigBean> dataSize) {
        super(R.layout.item_config, dataSize);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConfigBean configBean) {
        helper.setText(R.id.info_config, configBean.getVideoId());
    }

}
