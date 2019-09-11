package cn.com.venvy.svga.library;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;

import com.opensource.svgaplayer.proto.ShapeEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanjiangbo on 2018/3/26.
 *
 * DONE
 */

public class SVGAVideoShapeEntity {

    private Path path = new Path();

    enum Type {
        shape,
        rect,
        ellipse,
        keep
    }

    public class Styles {
        public int fill = 0x00000000;

        public int stroke = 0x00000000;

        public float strokeWidth = 0.0f;

        public String lineCap = "butt";

        public String lineJoin = "miter";

        public int miterLimit = 0;

        public float[] lineDash;
    }

    public Type type = Type.shape;

    public Map<String, Object> args = null;

    public Styles styles = null;

    public Matrix transform = null;

    public Path shapePath;

    public boolean isKeep() {
        return type == Type.keep;
    }

    public SVGAVideoShapeEntity(ShapeEntity shapeEntity) {
        parseType(shapeEntity);
        parseArgs(shapeEntity);
        parseStyles(shapeEntity);
        parseTransform(shapeEntity);

    }

    private void parseType(ShapeEntity shapeEntity) {
        ShapeEntity.ShapeType shapeType = shapeEntity != null ? shapeEntity.type : null;
        if (shapeType != null) {
            switch (shapeType) {
                case SHAPE:
                    this.type = Type.shape;
                    break;
                case RECT:
                    this.type = Type.rect;
                    break;
                case ELLIPSE:
                    this.type = Type.ellipse;
                    break;
                case KEEP:
                    this.type = Type.keep;
                    break;
            }
        }
    }

    private void parseArgs(ShapeEntity shapeEntity) {
        HashMap<String, Object> args = new HashMap<>();
        if (shapeEntity != null && shapeEntity.shape != null && shapeEntity.shape.d != null) {
            args.put("d", shapeEntity.shape.d);
        }
        if (shapeEntity != null && shapeEntity.ellipse != null) {
            args.put("x", shapeEntity.ellipse.x != null ? shapeEntity.ellipse.x : 0.0f);
            args.put("y", shapeEntity.ellipse.y != null ? shapeEntity.ellipse.y : 0.0f);
            args.put("radiusX", shapeEntity.ellipse.radiusX != null ? shapeEntity.ellipse.radiusX : 0.0f);
            args.put("radiusY", shapeEntity.ellipse.radiusY != null ? shapeEntity.ellipse.radiusY : 0.0f);
        }
        if (shapeEntity != null && shapeEntity.rect != null) {
            args.put("x", shapeEntity.rect.x != null ? shapeEntity.rect.x : 0.0f);
            args.put("y", shapeEntity.rect.y != null ? shapeEntity.rect.y : 0.0f);
            args.put("width", shapeEntity.rect.width != null ? shapeEntity.rect.width : 0.0f);
            args.put("height", shapeEntity.rect.height != null ? shapeEntity.rect.height : 0.0f);
            args.put("cornerRadius", shapeEntity.rect.cornerRadius != null ? shapeEntity.rect.cornerRadius : 0.0f);
        }
        this.args = args;
    }

    private void parseStyles(ShapeEntity shapeEntity) {
        if (shapeEntity != null && shapeEntity.styles != null) {
            Styles styles = new Styles();
            if (shapeEntity.styles.fill != null) {
                styles.fill = Color.argb((int) ((shapeEntity.styles.fill.a != null ? shapeEntity.styles.fill.a : 0.0f) * 255),
                        (int) ((shapeEntity.styles.fill.r != null ? shapeEntity.styles.fill.r : 0.0f) * 255),
                        (int) ((shapeEntity.styles.fill.g != null ? shapeEntity.styles.fill.g : 0.0f) * 255),
                        (int) ((shapeEntity.styles.fill.b != null ? shapeEntity.styles.fill.b : 0.0f) * 255));
            }
            if (shapeEntity.styles.stroke != null) {
                styles.stroke = Color.argb((int) ((shapeEntity.styles.stroke.a != null ? shapeEntity.styles.stroke.a : 0.0f) * 255),
                        (int) ((shapeEntity.styles.stroke.r != null ? shapeEntity.styles.stroke.r : 0.0f) * 255),
                        (int) ((shapeEntity.styles.stroke.g != null ? shapeEntity.styles.stroke.g : 0.0f) * 255),
                        (int) ((shapeEntity.styles.stroke.b != null ? shapeEntity.styles.stroke.b : 0.0f) * 255));
            }
            styles.strokeWidth = shapeEntity.styles.strokeWidth != null ? shapeEntity.styles.strokeWidth : 0.0f;
            if (shapeEntity.styles.lineCap != null) {
                switch (shapeEntity.styles.lineCap) {
                    case LineCap_BUTT:
                        styles.lineCap = "butt";
                        break;
                    case LineCap_ROUND:
                        styles.lineCap = "round";
                        break;
                    case LineCap_SQUARE:
                        styles.lineCap = "square";
                        break;
                }
            }
            if (shapeEntity.styles.lineJoin != null) {
                switch (shapeEntity.styles.lineJoin) {
                    case LineJoin_BEVEL:
                        styles.lineJoin = "bevel";
                        break;
                    case LineJoin_MITER:
                        styles.lineJoin = "miter";
                        break;
                    case LineJoin_ROUND:
                        styles.lineJoin = "round";
                        break;
                }
            }
            styles.miterLimit = shapeEntity.styles.miterLimit != null ? (shapeEntity.styles.miterLimit).intValue() : 0;
            styles.lineDash = new float[3];
            styles.lineDash[0] = shapeEntity.styles.lineDashI != null ? shapeEntity.styles.lineDashI : 0;
            styles.lineDash[1] = shapeEntity.styles.lineDashII != null ? shapeEntity.styles.lineDashII : 0;
            styles.lineDash[2] = shapeEntity.styles.lineDashIII != null ? shapeEntity.styles.lineDashIII : 0;
            this.styles = styles;
        }

    }

    private void parseTransform(ShapeEntity shapeEntity) {
        if (shapeEntity != null && shapeEntity.transform != null) {
            Matrix transform = new Matrix();
            float[] aar = new float[9];
            aar[0] = shapeEntity.transform.a != null ? shapeEntity.transform.a : 1.0f;
            aar[1] = shapeEntity.transform.c != null ? shapeEntity.transform.c : 0.0f;
            aar[2] = shapeEntity.transform.tx != null ? shapeEntity.transform.tx : 0.0f;
            aar[3] = shapeEntity.transform.b != null ? shapeEntity.transform.b : 0.0f;
            aar[4] = shapeEntity.transform.d != null ? shapeEntity.transform.d : 1.0f;
            aar[5] = shapeEntity.transform.ty != null ? shapeEntity.transform.ty : 0.0f;
            aar[6] = 0.0f;
            aar[7] = 0.0f;
            aar[8] = 1.0f;
            transform.setValues(aar);
            this.transform = transform;
        }
    }

    public void buildPath() {
        if (this.shapePath != null) {
            return;
        }
        path.reset();
        float xv;
        float yv;
        float wv;
        float hv;
        float crv;
        float rx;
        float ry;

        if (this.type == Type.shape) {
            if (args != null) {
                Object object = args.get("d");
                if (object != null && object instanceof String) {
                    new SVGAPath((String) object).buildPath(path);
                }
            }
        } else if (this.type == Type.ellipse) {
            if (args != null) {
                Object xObject = args.get("x");
                Object yObject = args.get("y");
                Object raxObject = args.get("radiusX");
                Object rayObject = args.get("radiusY");
                if (!(xObject instanceof Number) ||
                        !(yObject instanceof Number) ||
                        !(raxObject instanceof Number) ||
                        !(rayObject instanceof Number)) {
                    return;
                }
                xv = ((Number) xObject).floatValue();
                yv = ((Number) yObject).floatValue();
                rx = ((Number) raxObject).floatValue();
                ry = ((Number) rayObject).floatValue();
                path.addOval(new RectF(xv - rx, yv - ry, xv + rx, yv + ry), Path.Direction.CW);
            }
        } else if (this.type == Type.rect) {
            Object xObject = args.get("x");
            Object yObject = args.get("y");
            Object widthObject = args.get("width");
            Object heightObject = args.get("height");
            Object conRadiusObject = args.get("cornerRadius");
            if (!(xObject instanceof Number) ||
                    !(yObject instanceof Number) ||
                    !(widthObject instanceof Number) ||
                    !(heightObject instanceof Number) ||
                    !(conRadiusObject instanceof Number)) {
                return;
            }
            xv = ((Number) xObject).floatValue();
            yv = ((Number) yObject).floatValue();
            wv = ((Number) widthObject).floatValue();
            hv = ((Number) heightObject).floatValue();
            crv = ((Number) conRadiusObject).floatValue();
            path.addRoundRect(new RectF(xv, yv, xv + wv, yv + hv), crv, crv, Path.Direction.CW);
        }
        this.shapePath = new Path();
        shapePath.set(path);
    }
}
