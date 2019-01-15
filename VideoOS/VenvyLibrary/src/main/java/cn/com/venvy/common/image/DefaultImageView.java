package cn.com.venvy.common.image;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by mac on 18/3/1.
 */

public class DefaultImageView extends ImageView implements IImageView {
    public DefaultImageView(Context context) {
        super(context);
    }

    public DefaultImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DefaultImageView(Context context, AttributeSet attributeSet, int style) {
        super(context, attributeSet, style);
    }

    public View getImageView() {
        return this;
    }
}
