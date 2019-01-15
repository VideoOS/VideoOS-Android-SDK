package cn.com.venvy.common.fresco;

import android.content.Context;
import android.view.View;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import cn.com.venvy.common.image.IImageView;

/**
 * Created by mac on 18/2/28.
 */

public class VenvyFrescoImageView extends SimpleDraweeView implements IImageView {
    public VenvyFrescoImageView(Context context) {
        super(context);
    }

    @Override
    public View getImageView() {
        return this;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            return;
        }
        ScalingUtils.ScaleType tempScaleType = null;
        switch (scaleType) {
            case CENTER_INSIDE:
                tempScaleType = ScalingUtils.ScaleType.CENTER_INSIDE;
                break;

            case FIT_XY:
                tempScaleType = ScalingUtils.ScaleType.FIT_XY;
                break;

            case FIT_START:
                tempScaleType = ScalingUtils.ScaleType.FIT_START;
                break;

            case FIT_CENTER:
                tempScaleType = ScalingUtils.ScaleType.FIT_CENTER;
                break;

            case FIT_END:
                tempScaleType = ScalingUtils.ScaleType.FIT_END;
                break;
            case CENTER:
                tempScaleType = ScalingUtils.ScaleType.CENTER;
                break;
            case CENTER_CROP:
                tempScaleType = ScalingUtils.ScaleType.CENTER_CROP;
                break;
        }

        GenericDraweeHierarchy hierarchy = getHierarchy();
        if (hierarchy == null) {
            GenericDraweeHierarchyBuilder hierarchyBuilder =
                    new GenericDraweeHierarchyBuilder(getResources());
            hierarchyBuilder.setActualImageScaleType(tempScaleType);
            hierarchy = hierarchyBuilder.build();
            setHierarchy(hierarchy);
        } else {
            hierarchy.setActualImageScaleType(tempScaleType);
        }

    }
}
