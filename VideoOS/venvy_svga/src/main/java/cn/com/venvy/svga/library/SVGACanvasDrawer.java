package cn.com.venvy.svga.library;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yanjiangbo on 2018/3/27.
 * Done
 */

public class SVGACanvasDrawer extends SVGADrawer {

    public int canvasW = 0;
    public int canvasH = 0;
    public Paint sharedPaint = new Paint();
    public Path sharedPath = new Path();
    public Path sharedPath2 = new Path();
    public Matrix sharedShapeMatrix = new Matrix();
    public Matrix sharedFrameMatrix = new Matrix();
    public HashMap<String, Bitmap> drawTextCache;
    public HashMap<SVGAVideoShapeEntity, Path> drawPathCache;
    private SVGADynamicEntity dynamicEntity;
    private SVGAVideoEntity videoEntity;
    private float[] tValues = new float[16];

    public SVGACanvasDrawer(SVGAVideoEntity videoEntity, SVGADynamicEntity dynamicEntity) {
        super(videoEntity);
        this.videoEntity = videoEntity;
        this.dynamicEntity = dynamicEntity;
        drawTextCache = new HashMap<>();
        drawPathCache = new HashMap<>();
    }

    public void clearCache() {
        if (drawTextCache != null) {
            drawTextCache.clear();
        }
        if (drawPathCache != null) {
            drawPathCache.clear();
        }
        sharedShapeMatrix = null;
        sharedFrameMatrix = null;
        sharedPaint = null;
        sharedPath = null;
        sharedPath2 = null;
        dynamicEntity = null;
        videoEntity = null;
        tValues = null;

    }

    @Override
    public void drawFrame(Canvas canvas, int frameIndex, ImageView.ScaleType scaleType) {
        super.drawFrame(canvas, frameIndex, scaleType);
        resetCachePath(canvas);
        requestFrameSprites(canvas, frameIndex);
    }

    private void requestFrameSprites(Canvas canvas, int frameIndex) {
        if (videoEntity != null) {
            List<SVGAVideoSpriteEntity> sprites = videoEntity.sprites;
            if (sprites != null) {
                for (SVGAVideoSpriteEntity entity : sprites) {
                    if (entity.frames != null && frameIndex < entity.frames.size()) {
                        SVGAVideoSpriteFrameEntity frameEntity = entity.frames.get(frameIndex);
                        if (frameEntity != null && frameEntity.alpha > 0.0) {
                            drawSprite(entity.imageKey, frameEntity, canvas, frameIndex);
                        }
                    }
                }
            }
        }
    }

    private void resetCachePath(Canvas canvas) {
        if (canvasW != canvas.getWidth() || canvasH != canvas.getHeight()) {
            drawPathCache.clear();
        }
        canvasW = canvas.getWidth();
        canvasH = canvas.getHeight();
    }

    private void resetShareMatrix(Matrix transform) {
        if (sharedFrameMatrix == null) {
            return;
        }
        sharedFrameMatrix.reset();
        sharedFrameMatrix.postScale(scaleEntity.scaleFx, scaleEntity.scaleFy);
        sharedFrameMatrix.postTranslate(scaleEntity.tranFx, scaleEntity.tranFy);
        sharedFrameMatrix.preConcat(transform);
    }

    private void drawSprite(String imageKey, SVGAVideoSpriteFrameEntity frameEntity, Canvas canvas, int frameIndex) {
        drawImage(imageKey, frameEntity, canvas);
        drawShape(imageKey, frameEntity, canvas);
        drawDynamic(imageKey, frameEntity, canvas, frameIndex);

    }

    private void drawDynamic(String imageKey, SVGAVideoSpriteFrameEntity frameEntity, Canvas canvas, int frameIndex) {
        if (TextUtils.isEmpty(imageKey)) {
            return;
        }
        if (dynamicEntity == null) {
            return;
        }
        SVGAMethod drawer = dynamicEntity.getDynamicDrawer() != null && dynamicEntity.getDynamicDrawer().get(imageKey) != null ? dynamicEntity.getDynamicDrawer().get(imageKey) : null;
        if (drawer != null) {
            resetShareMatrix(frameEntity.transform);
            canvas.save();
            canvas.concat(sharedFrameMatrix);
            drawer.call(canvas, frameIndex);
            canvas.restore();
        }
    }


    private void drawImage(String imageKey, SVGAVideoSpriteFrameEntity frameEntity, Canvas canvas) {
        if (TextUtils.isEmpty(imageKey)) {
            return;
        }
        if (videoEntity == null) {
            return;
        }
        if (dynamicEntity == null) {
            return;
        }
        Boolean b = dynamicEntity.getDynamicHidden() != null ? dynamicEntity.getDynamicHidden().get(imageKey) : null;
        if (b != null && b) {
            return;
        }
        Bitmap bitmap = dynamicEntity.getDynamicImage() != null ? dynamicEntity.getDynamicImage().get(imageKey) : null;
        if (bitmap == null) {
            bitmap = videoEntity.images != null ? videoEntity.images.get(imageKey) : null;
        }
        if (bitmap != null) {
            resetShareMatrix(frameEntity.transform);
            sharedPaint.reset();
            sharedPaint.setAntiAlias(videoEntity.antiAlias);
            sharedPaint.setFilterBitmap(videoEntity.antiAlias);
            sharedPaint.setAlpha((int) (frameEntity.alpha * 255));
            SVGAPath maskPath = frameEntity.maskPath;
            if (maskPath != null) {
                canvas.save();
                sharedPath.reset();
                maskPath.buildPath(sharedPath);
                sharedPath.transform(sharedFrameMatrix);
                canvas.clipPath(sharedPath);
                if (bitmap.getWidth() != 0) {
                    sharedFrameMatrix.preScale((float) (frameEntity.layout.width / bitmap.getWidth()), (float) (frameEntity.layout.width / bitmap.getWidth()));
                }
                canvas.drawBitmap(bitmap, sharedFrameMatrix, sharedPaint);
                canvas.restore();
            } else {
                if (bitmap.getWidth() != 0) {
                    sharedFrameMatrix.preScale((float) (frameEntity.layout.width / bitmap.getWidth()), (float) (frameEntity.layout.width / bitmap.getWidth()));
                }
                canvas.drawBitmap(bitmap, sharedFrameMatrix, sharedPaint);
            }
            drawText(canvas, bitmap, imageKey, frameEntity);
        }
    }

    private void drawText(Canvas canvas, Bitmap drawingBitmap, String imageKey, SVGAVideoSpriteFrameEntity frameEntity) {
        if (dynamicEntity == null) {
            return;
        }
        if (dynamicEntity.isTextDirty()) {
            if (drawTextCache != null) {
                this.drawTextCache.clear();
            }
            this.dynamicEntity.setTextDirty(false);
        }
        if (TextUtils.isEmpty(imageKey)) {
            return;
        }
        Bitmap bitmap = drawTextCache.get(imageKey);
        String drawingText = dynamicEntity.getDynamicText().get(imageKey);
        if (!TextUtils.isEmpty(drawingText)) {
            TextPaint drawingTextPaint = dynamicEntity.getDynamicTextPaint().get(imageKey);
            if (drawingTextPaint != null) {
                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(drawingBitmap.getWidth(), drawingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas textCanvas = new Canvas(bitmap);
                    drawingTextPaint.setAntiAlias(true);
                    Rect bounds = new Rect();
                    drawingTextPaint.getTextBounds(drawingText, 0, drawingText.length(), bounds);
                    double x = (drawingBitmap.getWidth() - bounds.width()) / 2.0;
                    double targetRectTop = 0;
                    double targetRectBottom = drawingBitmap.getHeight();
                    double y = (targetRectBottom + targetRectTop - drawingTextPaint.getFontMetrics().bottom - drawingTextPaint.getFontMetrics().top) / 2;
                    textCanvas.drawText(drawingText, (float) x, (float) y, drawingTextPaint);
                    drawTextCache.put(imageKey, bitmap);
                }
            }
        }
        StaticLayout staticLayout = this.dynamicEntity.getDynamicLayoutText() != null ? this.dynamicEntity.getDynamicLayoutText().get(imageKey) : null;
        if (staticLayout != null) {
            if (bitmap == null) {
                staticLayout.getPaint().setAntiAlias(true);
                StaticLayout maskPath2 = new StaticLayout(staticLayout.getText(), 0, staticLayout.getText().length(), staticLayout.getPaint(), drawingBitmap.getWidth(), staticLayout.getAlignment(), staticLayout.getSpacingMultiplier(), staticLayout.getSpacingAdd(), false);
                bitmap = Bitmap.createBitmap(drawingBitmap.getWidth(), drawingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas bitmapShader = new Canvas(bitmap);
                bitmapShader.translate(0.0F, (float) ((drawingBitmap.getHeight() - maskPath2.getHeight()) / 2));
                maskPath2.draw(bitmapShader);
                drawTextCache.put(imageKey, bitmap);
            }
        }


        if (bitmap != null) {
            sharedPaint.reset();
            sharedPaint.setAntiAlias(videoEntity.antiAlias);
            SVGAPath maskPath = frameEntity.maskPath;
            if (maskPath != null) {
                canvas.save();
                canvas.concat(sharedFrameMatrix);
                canvas.clipRect(0, 0, drawingBitmap.getWidth(), drawingBitmap.getHeight());
                BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                this.sharedPaint.setShader(bitmapShader);
                this.sharedPath.reset();
                maskPath.buildPath(this.sharedPath);
                canvas.drawPath(this.sharedPath, this.sharedPaint);
                canvas.restore();
            } else {
                sharedPaint.setFilterBitmap(videoEntity.antiAlias);
                canvas.drawBitmap(bitmap, sharedFrameMatrix, sharedPaint);
            }
        }
    }

    private void drawShape(String imageKey, SVGAVideoSpriteFrameEntity frameEntity, Canvas canvas) {
        if (frameEntity == null) {
            return;
        }
        if (videoEntity == null) {
            return;
        }
        resetShareMatrix(frameEntity.transform);
        List<SVGAVideoShapeEntity> shapes = frameEntity.shapes;
        if (shapes == null) {
            return;
        }
        for (SVGAVideoShapeEntity shape : shapes) {
            shape.buildPath();
            if (shape.shapePath != null) {
                this.sharedPaint.reset();
                this.sharedPaint.setAntiAlias(videoEntity.antiAlias);
                this.sharedPaint.setAlpha((int) frameEntity.alpha * 255);
                if (!drawPathCache.containsKey(shape)) {
                    Path path = new Path();
                    path.set(shape.shapePath);
                    drawPathCache.put(shape, path);

                }
                sharedPath.reset();
                sharedPath.addPath(new Path(drawPathCache.get(shape)));
                sharedShapeMatrix.reset();
                if (shape.transform != null) {
                    sharedShapeMatrix.postConcat(shape.transform);
                }
                sharedShapeMatrix.postConcat(sharedFrameMatrix);
                sharedPath.transform(sharedShapeMatrix);
            }
            if (shape.styles != null) {
                if (shape.styles.fill != 0x00000000) {
                    sharedPaint.setColor(shape.styles.fill);
                    SVGAPath maskPath = frameEntity.maskPath;
                    if (maskPath != null) {
                        canvas.save();
                        sharedPath2.reset();
                        maskPath.buildPath(sharedPath2);
                        sharedPath2.transform(this.sharedFrameMatrix);
                        canvas.clipPath(sharedPath2);
                    }
                    canvas.drawPath(sharedPath, sharedPaint);
                    if (maskPath != null) {
                        canvas.restore();
                    }
                }
                if (shape.styles.strokeWidth > 0) {
                    resetShapeStrokePaint(shape);
                    SVGAPath maskPath = frameEntity.maskPath;
                    if (maskPath != null) {
                        canvas.save();
                        sharedPath2.reset();
                        maskPath.buildPath(sharedPath2);
                        sharedPath2.transform(this.sharedFrameMatrix);
                        canvas.clipPath(sharedPath2);
                    }
                    canvas.drawPath(sharedPath, sharedPaint);
                    if (maskPath != null) canvas.restore();
                }
            }
        }
    }

    private float requestScale() {
        this.sharedFrameMatrix.getValues(tValues);
        if (tValues[0] == 0f) {
            return 0f;
        }
        double A = tValues[0];
        double B = tValues[3];
        double C = tValues[1];
        double D = tValues[4];
        if (A * D == B * C) return 0f;
        double scaleX = Math.sqrt(A * A + B * B);
        A /= scaleX;
        B /= scaleX;
        double skew = A * C + B * D;
        C -= A * skew;
        D -= B * skew;
        double scaleY = Math.sqrt(C * C + D * D);
        C /= scaleY;
        D /= scaleY;
        skew /= scaleY;
        if (A * D < B * C) {
            scaleX = -scaleX;
        }
        return scaleX != 0 && scaleY != 0 ? ((scaleEntity.ratioX ? scaleEntity.ratio / Math.abs((float) scaleX) : scaleEntity.ratio / Math.abs((float) scaleY))) : 0;
    }

    private void resetShapeStrokePaint(SVGAVideoShapeEntity shape) {
        sharedPaint.reset();
        sharedPaint.setAntiAlias(videoEntity.antiAlias);
        sharedPaint.setStyle(Paint.Style.STROKE);
        if (shape.styles != null) {
            sharedPaint.setColor(shape.styles.stroke);
            float scale = requestScale();
            sharedPaint.setStrokeMiter(shape.styles.miterLimit * scale);

            sharedPaint.setStrokeWidth(shape.styles.strokeWidth);
            if (shape.styles.lineCap != null) {
                switch (shape.styles.lineCap.toLowerCase()) {
                    case "butt":
                        sharedPaint.setStrokeCap(Paint.Cap.BUTT);
                        break;
                    case "round":
                        sharedPaint.setStrokeCap(Paint.Cap.ROUND);
                        break;

                    case "square":
                        sharedPaint.setStrokeCap(Paint.Cap.SQUARE);
                        break;
                }
            }
            if (shape.styles.lineJoin != null) {
                switch (shape.styles.lineJoin.toLowerCase()) {
                    case "miter":
                        sharedPaint.setStrokeJoin(Paint.Join.MITER);
                        break;
                    case "round":
                        sharedPaint.setStrokeJoin(Paint.Join.ROUND);
                        break;
                    case "bevel":
                        sharedPaint.setStrokeJoin(Paint.Join.BEVEL);
                        break;
                }
            }
            float[] it = shape.styles.lineDash;
            if (it != null) {
                if (it.length == 3 && (it[0] > 0 || it[1] > 0)) {
                    float[] target = new float[2];
                    target[0] = it[0] < 1.0f ? 1.0f : it[0] * scale;
                    target[1] = it[1] < 1.0f ? 0.1f : it[1] * scale;
                    sharedPaint.setPathEffect(new DashPathEffect(target, it[2] * scale));
                }
            }
        }
    }
}
