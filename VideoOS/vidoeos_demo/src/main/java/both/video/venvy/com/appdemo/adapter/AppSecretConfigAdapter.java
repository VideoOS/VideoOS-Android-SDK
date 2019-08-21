package both.video.venvy.com.appdemo.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import both.video.venvy.com.appdemo.R;
import both.video.venvy.com.appdemo.bean.ConfigBean;

/**
 * cn.com.asmp.adapter
 */
public class AppSecretConfigAdapter extends BaseQuickAdapter<ConfigBean, BaseViewHolder> {
    public AppSecretConfigAdapter(List<ConfigBean> dataSize) {
        super(R.layout.item_config, dataSize);
    }

    @Override
    protected void convert(BaseViewHolder helper, ConfigBean item) {
        String appSecret = item.getAppSecret();
        helper.setText(R.id.info_config, TextUtils.isEmpty(appSecret) ? "" : appSecret);
    }

}
