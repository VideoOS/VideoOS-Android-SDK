package cn.com.venvy.svga.library;

import android.graphics.Path;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by yanjiangbo on 2018/3/26.
 */

public class SVGAPath {

    private static final Set<String> VALID_METHODS = new HashSet<>();

    static {
        VALID_METHODS.add("M");
        VALID_METHODS.add("L");
        VALID_METHODS.add("H");
        VALID_METHODS.add("V");
        VALID_METHODS.add("C");
        VALID_METHODS.add("S");
        VALID_METHODS.add("Q");
        VALID_METHODS.add("R");
        VALID_METHODS.add("A");
        VALID_METHODS.add("Z");
        VALID_METHODS.add("m");
        VALID_METHODS.add("l");
        VALID_METHODS.add("h");
        VALID_METHODS.add("v");
        VALID_METHODS.add("c");
        VALID_METHODS.add("s");
        VALID_METHODS.add("q");
        VALID_METHODS.add("r");
        VALID_METHODS.add("a");
        VALID_METHODS.add("z");
    }

    private String replacedValue;
    private Path cachedPath;


    public SVGAPath(String paramsString) {
        replacedValue = paramsString != null && paramsString.contains(",") ? paramsString.replace(",", " ") : paramsString;
    }

    public void buildPath(Path toPath) {
        if (cachedPath != null) {
            toPath.set(cachedPath);
            return;
        }
        if (this.replacedValue == null) {
            return;
        }
        Path cachedPath = new Path();
        StringTokenizer segments = new StringTokenizer(this.replacedValue, "MLHVCSQRAZmlhvcsqraz", true);
        String currentMethod = "";
        while (segments.hasMoreTokens()) {
            String segment = segments.nextToken();
            if (TextUtils.isEmpty(segment)) {
                continue;
            }
            if (VALID_METHODS.contains(segment)) {
                currentMethod = segment;
                if ("Z".equals(currentMethod) || "z".equals(currentMethod)) {
                    operate(cachedPath, currentMethod, new StringTokenizer("", ""));
                }
            } else {
                operate(cachedPath, currentMethod, new StringTokenizer(segment, " "));
            }
        }
        this.cachedPath = cachedPath;
        toPath.set(cachedPath);
    }

    private void operate(Path finalPath, String method, StringTokenizer args) {
        float x0 = 0.0f;
        float y0 = 0.0f;
        float x1 = 0.0f;
        float y1 = 0.0f;
        float x2 = 0.0f;
        float y2 = 0.0f;
        try {
            int index = 0;
            while (args.hasMoreTokens()) {
                String s = args.nextToken();
                if (TextUtils.isEmpty(s)) {
                    continue;
                }
                if (index == 0) {
                    x0 = Float.valueOf(s);
                }
                if (index == 1) {
                    y0 = Float.valueOf(s);
                }
                if (index == 2) {
                    x1 = Float.valueOf(s);
                }
                if (index == 3) {
                    y1 = Float.valueOf(s);
                }
                if (index == 4) {
                    x2 = Float.valueOf(s);
                }
                if (index == 5) {
                    y2 = Float.valueOf(s);
                }
                index++;
            }
        } catch (Exception e) {
        }
        SVGAPoint currentPoint = new SVGAPoint(0.0f, 0.0f, 0.0f);
        if ("M".equals(method)) {
            finalPath.moveTo(x0, y0);
            currentPoint = new SVGAPoint(x0, y0, 0.0f);
        } else if ("m".equals(method)) {
            finalPath.rMoveTo(x0, y0);
            currentPoint = new SVGAPoint(currentPoint.x + x0, currentPoint.y + y0, 0.0f);
        }
        if ("L".equals(method)) {
            finalPath.lineTo(x0, y0);
        } else if ("l".equals(method)) {
            finalPath.rLineTo(x0, y0);
        }
        if ("C".equals(method)) {
            finalPath.cubicTo(x0, y0, x1, y1, x2, y2);
        } else if ("c".equals(method)) {
            finalPath.rCubicTo(x0, y0, x1, y1, x2, y2);
        }
        if ("Q".equals(method)) {
            finalPath.quadTo(x0, y0, x1, y1);
        } else if ("q".equals(method)) {
            finalPath.rQuadTo(x0, y0, x1, y1);
        }
        if ("H".equals(method)) {
            finalPath.lineTo(x0, currentPoint.y);
        } else if ("h".equals(method)) {
            finalPath.rLineTo(x0, 0f);
        }
        if ("V".equals(method)) {
            finalPath.lineTo(currentPoint.x, x0);
        } else if ("v".equals(method)) {
            finalPath.rLineTo(0f, x0);
        }
        if ("Z".equals(method)) {
            finalPath.close();
        } else if ("z".equals(method)) {
            finalPath.close();
        }

    }


}
