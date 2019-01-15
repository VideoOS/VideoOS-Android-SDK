package cn.com.venvy.lua;

import com.taobao.luaview.global.LuaScriptLoader.ScriptExecuteCallback;
import com.taobao.luaview.scriptbundle.ScriptBundle;

import org.luaj.vm2.LuaValue;

/**
 * Created by Arthur on 2017/8/22.
 */

public class LuaCallback implements ScriptExecuteCallback {
    @Override
    public boolean onScriptPrepared(ScriptBundle bundle) {
        return false;
    }

    @Override
    public boolean onScriptCompiled(LuaValue value, LuaValue context, LuaValue view) {
        return false;
    }

    @Override
    public void onScriptExecuted(String uri, boolean executedSuccess) {

    }
}
