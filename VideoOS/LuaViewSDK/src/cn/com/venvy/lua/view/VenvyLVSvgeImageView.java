package cn.com.venvy.lua.view;


import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.taobao.luaview.userdata.ui.UDView;
import com.taobao.luaview.util.LuaUtil;
import com.taobao.luaview.view.interfaces.ILVView;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Field;

import cn.com.venvy.common.image.VenvyImageFactory;
import cn.com.venvy.common.interf.ISvgaImageView;
import cn.com.venvy.common.interf.ISvgaParseCompletion;
import cn.com.venvy.common.interf.SVGACallback;
import cn.com.venvy.common.utils.VenvyLog;
import cn.com.venvy.lua.ud.VenvyUDSvgaImageView;

/**
 * Created by mac on 18/3/27.
 */

public class VenvyLVSvgeImageView extends FrameLayout implements ILVView {
    private UDView mLuaUserdata;
    LuaValue mOnPause;
    LuaValue mOnFinished;
    LuaValue mOnRepeat;
    LuaValue mOnStep;
    LuaValue mReadyToPlay;
    private int frames;
    private int fps;
    private ISvgaImageView iSvgaImageView;

    public VenvyLVSvgeImageView(Globals globals, LuaValue metaTable, Varargs varargs) {
        super(globals.getContext());
        this.mLuaUserdata = new VenvyUDSvgaImageView(this, globals, metaTable, varargs);

        iSvgaImageView = VenvyImageFactory.createSvgaImage(getContext());
        if (iSvgaImageView != null && iSvgaImageView instanceof View) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((View) iSvgaImageView).setLayoutParams(params);
            addView((View) iSvgaImageView);
        }
    }

    public void svga(final Varargs varargs) {
        if (varargs.isstring(2)) {
            final String url = varargs.optjstring(2, null);
            mReadyToPlay = varargs.optfunction(3, null);
            if (iSvgaImageView == null) {
                return;
            }
            iSvgaImageView.parse(url, new ISvgaParseCompletion() {
                @Override
                public void onComplete(Object videoItem) {
                    try {
                        Class videoItemClass = videoItem.getClass();
                        Field fpsField = videoItemClass.getDeclaredField("FPS");
                        Field framesField = videoItemClass.getDeclaredField("frames");
                        if (fpsField != null) {
                            fps = fpsField.getInt(videoItem);
                            frames = framesField.getInt(videoItem);
                        }
                        if (mReadyToPlay != null) {
                            LuaUtil.callFunction(mReadyToPlay);
                        } else {
                            iSvgaImageView.startAnimation();
                        }
                    } catch (Exception e) {
                        VenvyLog.e(VenvyLVSvgeImageView.class.getName(), e);
                    }
                }

                @Override
                public void onError() {

                }
            });

            iSvgaImageView.setCallback(new SVGACallback() {
                @Override
                public void onPause() {
                    LuaUtil.callFunction(mOnPause);
                }

                @Override
                public void onFinished() {
                    LuaUtil.callFunction(mOnFinished);
                }

                @Override
                public void onRepeat() {
                    LuaUtil.callFunction(mOnRepeat);
                }

                @Override
                public void onStep(int i, double v) {
                    LuaUtil.callFunction(mOnStep, LuaValue.valueOf(i));
                }
            });

        }
    }

    public void stepToPercentage(Varargs varargs) {
        if (iSvgaImageView == null) {
            return;
        }
        if (varargs == null || varargs.narg() <= 1) {
            return;
        }
        double percentage = varargs.optdouble(2, 0);
        boolean autoPlay = varargs.optboolean(3, true);
        iSvgaImageView.stepToPercentage(percentage, autoPlay);
    }

    public void stepToFrame(Varargs varargs) {
        if (iSvgaImageView == null) {
            return;
        }
        if (varargs == null || varargs.narg() <= 1) {
            return;
        }

        int frame = varargs.optint(2, 0);
        boolean autoPlay = varargs.optboolean(3, true);
        iSvgaImageView.stepToFrame(frame, autoPlay);
    }

    public void startAnimation(Varargs varargs) {
        if (iSvgaImageView == null) {
            return;
        }
        if (varargs == null) {
            iSvgaImageView.startAnimation();
        } else {
            int length = varargs.optint(2, -1);
            int location = varargs.optint(3, -1);
            if (length != -1 && location != -1) {
                boolean isReverse = varargs.optboolean(4, false);
                iSvgaImageView.startAnimation(length, location, isReverse);
            } else {
                iSvgaImageView.startAnimation();
            }
        }
    }

    public LuaValue isAnimation(Varargs varargs) {
        return LuaValue.valueOf(iSvgaImageView != null && iSvgaImageView.isAnimating());
    }

    public void readyToPlay(Varargs varargs) {
        mReadyToPlay = varargs.optfunction(2, null);
    }

    public void stopAnimation(Varargs varargs) {
        boolean b = varargs.optboolean(2, false);
        if (iSvgaImageView != null) {
            if (b) {
                iSvgaImageView.stopAnimation(b);
            }
            iSvgaImageView.stopAnimation();
        }
    }

    public void pauseAnimation(Varargs varargs) {
        if (iSvgaImageView != null) {
            iSvgaImageView.pauseAnimation();
        }
    }

    public LuaValue frames(Varargs varargs) {
        return LuaValue.valueOf(frames);
    }


    public LuaValue fps(Varargs varargs) {
        return LuaValue.valueOf(fps);
    }

    public void loops(Varargs varargs) {
        if (iSvgaImageView != null) {
            int loops = varargs.optint(2, 0);
            iSvgaImageView.setLoops(loops);
        }

    }

    public void setLuaCallback(LuaTable callbacks) {
        if (callbacks != null) {
            mOnPause = LuaUtil.getFunction(callbacks, "onPause", "onPause");
            mOnFinished = LuaUtil.getFunction(callbacks, "onFinished", "onFinished");
            mOnRepeat = LuaUtil.getFunction(callbacks, "onRepeat", "onRepeat");
            mOnStep = LuaUtil.getFunction(callbacks, "onStep", "onStep");
        }
    }

    @Override
    public UDView getUserdata() {
        return mLuaUserdata;
    }

}
