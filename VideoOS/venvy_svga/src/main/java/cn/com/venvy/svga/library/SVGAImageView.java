package cn.com.venvy.svga.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import java.lang.reflect.Field;
import java.net.URL;

import cn.com.venvy.common.interf.SVGACallback;
import cn.com.venvy.svga.R;


/**
 * Created by yanjiangbo on 2018/3/27.
 * Done
 */

public class SVGAImageView extends ImageView {

    public enum FillMode {
        Backward,
        Forward,
    }

    private ValueAnimator animator;
    private SVGACallback callback;
    private boolean clearsAfterStop = true;
    private FillMode fillMode = FillMode.Forward;
    private boolean isAnimating;
    private int loops;

    public SVGAImageView(Context paramContext) {
        super(paramContext);
        setSoftwareLayerType();
    }

    public SVGAImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setSoftwareLayerType();
        if (paramAttributeSet != null) {
            loadAttrs(paramAttributeSet);
        }
    }

    public SVGAImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        setSoftwareLayerType();
        if (paramAttributeSet != null) {
            loadAttrs(paramAttributeSet);
        }
    }

    @TargetApi(21)
    public SVGAImageView(Context context, AttributeSet paramAttributeSet, int defStyleAttr,
                         int defStyleRes) {
        super(context, paramAttributeSet, defStyleAttr, defStyleRes);
        setSoftwareLayerType();
        if (paramAttributeSet != null) {
            loadAttrs(paramAttributeSet);
        }
    }


    public void setCallback(SVGACallback callback) {
        this.callback = callback;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public void setClearsAfterStop(boolean clearsAfterStop) {
        this.clearsAfterStop = clearsAfterStop;
    }

    public void setFillMode(FillMode mode) {
        this.fillMode = mode;
    }

    private void setSoftwareLayerType() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation(true);
        Drawable drawable = getDrawable();
        if (drawable instanceof SVGADrawable) {
            ((SVGADrawable) drawable).clear();
        }
        super.onDetachedFromWindow();
    }


    private void loadAttrs(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.SVGAImageView, 0, 0);
        if (typedArray == null) {
            return;
        }
        this.loops = typedArray.getInt(R.styleable.SVGAImageView_loopCount, 0);
        this.clearsAfterStop = typedArray.getBoolean(R.styleable.SVGAImageView_clearsAfterStop, true);
        final boolean antiAlias = typedArray.getBoolean(R.styleable.SVGAImageView_antiAlias, true);
        final boolean autoPlay = typedArray.getBoolean(R.styleable.SVGAImageView_autoPlay, true);
        String fillMode = typedArray.getString(R.styleable.SVGAImageView_fillMode);
        if (!TextUtils.isEmpty(fillMode)) {
            if ("0".equals(fillMode)) {
                this.fillMode = FillMode.Backward;
            } else if ("1".equals(fillMode)) {
                this.fillMode = FillMode.Forward;
            }
        }

        final String source = typedArray.getString(R.styleable.SVGAImageView_source);
        if (!TextUtils.isEmpty(source)) {
            final SVGAParser parser = new SVGAParser(getContext());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SVGAParser.ParseCompletion parseCompletion = new SVGAParser.ParseCompletion() {
                        @Override
                        public void onComplete(final SVGAVideoEntity videoItem) {
                            SVGAParser.runOnUIThread(new Runnable() {
                                @Override
                                public void run() {
                                    videoItem.antiAlias = antiAlias;
                                    setVideoItem(videoItem);
                                    if (autoPlay) {
                                        startAnimation();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    };
                    if (source.startsWith("http://") || source.startsWith("https://")) {
                        try {
                            parser.parse(new URL(source), parseCompletion);
                        } catch (Exception e) {
                        }
                    } else {
                        parser.parse(source, parseCompletion);
                    }

                }
            }).start();
        }
        typedArray.recycle();
    }

    public void startAnimation() {
        startAnimation(null, false);
    }

    public void startAnimation(SVGARange range, boolean reverse) {
        stopAnimation(false);
        final Drawable drawable = getDrawable();
        if (!(drawable instanceof SVGADrawable)) {
            return;
        }
        ((SVGADrawable) drawable).setCleared(false);
        ((SVGADrawable) drawable).scaleType = getScaleType();
        final SVGAVideoEntity videoEntity = ((SVGADrawable) drawable).videoEntity;
        if (videoEntity != null) {
            double durationScale = 1.0;
            final int startFrame = Math.max(0, range != null ? range.location : 0);
            final int endFrame = Math.min(videoEntity.frames - 1, (range != null ? range.location : 0) + ((range != null && (range.length > 0) ? range.length : Integer.MAX_VALUE) - 1));
            ValueAnimator animator = ValueAnimator.ofInt(startFrame, endFrame);
            try {
                Class animatorClass = Class.forName("android.animation.ValueAnimator");
                if (animatorClass != null) {
                    Field field = animatorClass.getDeclaredField("sDurationScale");
                    if (field != null) {
                        field.setAccessible(true);
                        float scale = field.getFloat(animatorClass);
                        durationScale = scale;
                    }
                }
            } catch (Exception e) {
            }
            animator.setInterpolator(new LinearInterpolator());
            animator.setDuration((long) ((endFrame - startFrame + 1) * (1000 / videoEntity.FPS) / durationScale));
            animator.setRepeatCount(loops <= 0 ? 99999 : loops - 1);
            final SVGACallback svgaCallback = callback;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    ((SVGADrawable) drawable).setCurrentFrame((int) animation.getAnimatedValue());
                    if (svgaCallback != null) {
                        svgaCallback.onStep(((SVGADrawable) drawable).getCurrentFrame(), videoEntity.frames == 0 ? 0 : (((SVGADrawable) drawable).getCurrentFrame() + 1) / videoEntity.frames);
                    }
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isAnimating = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isAnimating = false;
                    stopAnimation();
                    if (!clearsAfterStop) {
                        if (fillMode == FillMode.Backward) {
                            ((SVGADrawable) drawable).setCurrentFrame(startFrame);
                        } else if (fillMode == FillMode.Forward) {
                            ((SVGADrawable) drawable).setCurrentFrame(endFrame);
                        }
                    }
                    if (svgaCallback != null) {
                        svgaCallback.onFinished();
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    if (svgaCallback != null) {
                        svgaCallback.onRepeat();
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    isAnimating = true;
                }

                @Override
                public void onAnimationPause(Animator animation) {
                    super.onAnimationPause(animation);
                }

                @Override
                public void onAnimationResume(Animator animation) {
                    super.onAnimationResume(animation);
                }
            });
            if (reverse) {
                animator.reverse();
            } else {
                animator.start();
            }
            this.animator = animator;
        }
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void pauseAnimation() {
        stopAnimation(false);
        if (this.callback != null) {
            this.callback.onPause();
        }
    }

    public void stopAnimation() {
        this.stopAnimation(clearsAfterStop);
    }

    public void stopAnimation(boolean clear) {
        if (animator != null) {
            animator.cancel();
            animator.removeAllUpdateListeners();
            animator.removeAllListeners();
        }
        Drawable drawable = getDrawable();
        if (drawable instanceof SVGADrawable) {
            ((SVGADrawable) drawable).setCleared(clear);
        }
    }

    public void setVideoItem(SVGAVideoEntity videoEntity) {
        setVideoItem(videoEntity, new SVGADynamicEntity());
    }

    public void setVideoItem(SVGAVideoEntity videoItem, SVGADynamicEntity dynamicEntity) {
        SVGADrawable drawable = new SVGADrawable(videoItem, dynamicEntity);
        drawable.setCleared(clearsAfterStop);
        setImageDrawable(drawable);
    }

    public void stepToFrame(int frame, boolean andPlay) {
        pauseAnimation();
        Drawable drawable = getDrawable();
        if (!(drawable instanceof SVGADrawable)) {
            return;
        }
        ((SVGADrawable) drawable).setCurrentFrame(frame);
        if (andPlay) {
            startAnimation();
            if (this.animator != null) {
                long d = (frame / ((SVGADrawable) drawable).videoEntity.frames);
                this.animator.setCurrentPlayTime((long) (Math.max(0.0f, Math.min(1.0f, d)) * animator.getDuration()));
            }
        }
    }

    public void stepToPercentage(Double percentage, boolean andPlay) {
        Drawable drawable = getDrawable();
        if (drawable instanceof SVGADrawable) {
            int frame = (int) (((SVGADrawable) drawable).videoEntity.frames * percentage);
            if (frame > 0 && frame >= ((SVGADrawable) drawable).videoEntity.frames) {
                frame = ((SVGADrawable) drawable).videoEntity.frames - 1;
            }
            stepToFrame(frame, andPlay);
        }
    }
}
