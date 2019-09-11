package cn.com.venvy.svga.library;

import android.graphics.Matrix;
import android.text.TextUtils;

import com.opensource.svgaplayer.proto.FrameEntity;
import com.opensource.svgaplayer.proto.Layout;
import com.opensource.svgaplayer.proto.ShapeEntity;
import com.opensource.svgaplayer.proto.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanjiangbo on 2018/3/26.
 * Done
 */

public class SVGAVideoSpriteFrameEntity {
    public double alpha;
    public SVGARect layout = new SVGARect(0.0, 0.0, 0.0, 0.0);
    public Matrix transform = new Matrix();
    public SVGAPath maskPath;
    public List<SVGAVideoShapeEntity> shapes;

    public SVGAVideoSpriteFrameEntity(FrameEntity frameEntity) {
        this.alpha = frameEntity != null && frameEntity.alpha != null ? frameEntity.alpha : 0.0;
        Layout targetLayout = frameEntity != null ? frameEntity.layout : null;
        if (targetLayout != null) {
            double x = targetLayout.x != null ? targetLayout.x : 0.0;
            double y = targetLayout.y != null ? targetLayout.y : 0.0;
            double w = targetLayout.width != null ? targetLayout.width : 0.0;
            double h = targetLayout.height != null ? targetLayout.height : 0.0;
            this.layout = new SVGARect(x, y, w, h);
        }
        Transform transform = frameEntity != null ? frameEntity.transform : null;
        if (transform != null) {
            float[] aar = new float[9];
            aar[0] = transform.a != null ? transform.a : 1.0f;
            aar[1] = transform.c != null ? transform.c : 0.0f;
            aar[2] = transform.tx != null ? transform.tx : 0.0f;
            aar[3] = transform.b != null ? transform.b : 0.0f;
            aar[4] = transform.d != null ? transform.d : 1.0f;
            aar[5] = transform.ty != null ? transform.ty : 0.0f;
            aar[6] = 0.0f;
            aar[7] = 0.0f;
            aar[8] = 1.0f;
            this.transform.setValues(aar);
        }
        String clipPath = frameEntity != null ? frameEntity.clipPath : null;
        if (!TextUtils.isEmpty(clipPath)) {
            this.maskPath = new SVGAPath(clipPath);
        }

        List<ShapeEntity> shapes = frameEntity != null ? frameEntity.shapes : null;
        if (shapes != null) {
            for (ShapeEntity shapeEntity : shapes) {
                if (this.shapes == null) {
                    this.shapes = new ArrayList<>();
                }
                this.shapes.add(new SVGAVideoShapeEntity(shapeEntity));
            }
        }
    }

}
