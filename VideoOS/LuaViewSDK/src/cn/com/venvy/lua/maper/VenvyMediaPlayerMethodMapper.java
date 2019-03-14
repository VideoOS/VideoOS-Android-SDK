package cn.com.venvy.lua.maper;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.fun.mapper.ui.UIViewGroupMethodMapper;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.List;

import cn.com.venvy.lua.ud.VenvyUDMediaPlayerView;


/**
 * Created by lgf on 2018/1/19.
 */
@LuaViewLib(revisions = {"20180410"})
public class VenvyMediaPlayerMethodMapper<U extends VenvyUDMediaPlayerView> extends UIViewGroupMethodMapper<U> {
    private static final String TAG = "VenvyMediaPlayerMethodMapper";
    private static final String[] sMethods = new String[]{
            "startPlay",//0
            "stopPlay",//1
            "pausePlay",//2
            "restartPlay",//3
            "source",//4
            "position",//5
            "status",//6
            "duration",//7
            "voice"//8
    };

    @Override
    public List<String> getAllFunctionNames() {
        return mergeFunctionNames(TAG, super.getAllFunctionNames(), sMethods);
    }

    @Override
    public Varargs invoke(int code, U target, Varargs varargs) {
        final int optcode = code - super.getAllFunctionNames().size();
        switch (optcode) {
            case 0:
                return startPlay(target, varargs);
            case 1:
                return stopPlay(target, varargs);
            case 2:
                return pausePlay(target, varargs);
            case 3:
                return restartPlay(target, varargs);
            case 4:
                return source(target, varargs);
            case 5:
                return position(target, varargs);
            case 6:
                return status(target, varargs);
            case 7:
                return duration(target, varargs);
            case 8:
                return voice(target, varargs);

        }
        return super.invoke(code, target, varargs);
    }

    /**
     * 获取视频总长度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue duration(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        if (view == null) {
            return LuaValue.NIL;
        }
        return valueOf(view.getDuration());
    }

    /**
     * 设置、获取视频声音大小，取值范围0~1
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue voice(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        if (view == null) {
            return LuaValue.NIL;
        }
        if (varargs.narg() > 1) {
            Float voice = LuaUtil.getFloat(varargs, 2);
            if (voice != null) {
                view.setVoice(voice);
            }
        } else {
            return valueOf(view.getVoice());
        }
        return this;
    }

    /**
     * 开始播放视频
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue startPlay(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        if (view == null) {
            return LuaValue.NIL;
        }
        String url = LuaUtil.getString(varargs, 2);
        if (!TextUtils.isEmpty(url)) {
            view.startPlay(url);
        }
        return this;
    }

    /**
     * 停止播放视频
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue stopPlay(U view, Varargs varargs) {
        if (view == null) {
            return LuaValue.NIL;
        }
        view.stopPlay();
        return this;
    }

    /**
     * 暂停播放视频
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue pausePlay(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        if (view == null) {
            return LuaValue.NIL;
        }
        view.pausePlay();
        return this;
    }

    /**
     * 重新开始播放视频
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue restartPlay(U view, Varargs varargs) {
        if (varargs == null) {
            return this;
        }
        if (view == null) {
            return LuaValue.NIL;
        }
        view.restartPlay();
        return this;

    }


    /**
     * 获取当前播放视频资源地址
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue source(U view, Varargs varargs) {
        if (view == null) {
            return LuaValue.NIL;
        }
        return valueOf(view.getSource());
    }

    /**
     * 获取当前播放进度
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue position(U view, Varargs varargs) {
        if (view == null) {
            return LuaValue.NIL;
        }
        return valueOf(view.getPosition());
    }

    /**
     * 获取当前播放状态
     *
     * @param view
     * @param varargs
     * @return
     */
    public LuaValue status(U view, Varargs varargs) {
        if (view == null) {
            return LuaValue.NIL;
        }
        return valueOf(view.getStatus());
    }
}
