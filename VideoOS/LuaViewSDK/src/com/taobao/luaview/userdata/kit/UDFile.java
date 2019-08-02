/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root
 * directory of this source tree.
 */

package com.taobao.luaview.userdata.kit;

import android.text.TextUtils;

import com.taobao.luaview.fun.mapper.LuaViewApi;
import com.taobao.luaview.fun.mapper.LuaViewLib;
import com.taobao.luaview.global.LuaResourceFinder;
import com.taobao.luaview.global.VmVersion;
import com.taobao.luaview.scriptbundle.asynctask.SimpleTask1;
import com.taobao.luaview.userdata.base.BaseLuaTable;
import com.taobao.luaview.util.FileUtil;
import com.taobao.luaview.util.LuaUtil;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.io.File;

import cn.com.venvy.App;
import cn.com.venvy.common.utils.VenvyFileUtil;
import cn.com.venvy.common.utils.VenvyIOUtils;

/**
 * File 用户数据封装
 *
 * @author song
 * @date 15/9/6
 */
@LuaViewLib(revisions = {"20170306已对标"})
@LuaViewApi(since = VmVersion.V_550)
public class UDFile extends BaseLuaTable {

    public UDFile(Globals globals, LuaValue metatable) {
        super(globals, metatable);
        init();
    }

    private void init() {
        set("save", new save());
        set("read", new read());
        set("exists", new exists());
        set("path", new path());
    }

    private int fixIndex(Varargs args) {
        return args != null && args.arg1() instanceof UDFile ? 1 : 0;
    }

    /**
     * 保存文件
     */
    class save extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = fixIndex(args);
            if (args.narg() > fixIndex) {
                final LuaResourceFinder finder = getLuaResourceFinder();
                if (finder != null) {
                    final LuaValue param1 = args.arg(fixIndex + 1);
                    final LuaValue param2 = args.arg(fixIndex + 2);

                    String name = null;
                    byte[] data = null;
                    if (LuaUtil.isString(param1)) {
                        name = param1.optjstring(null);

                        if (param2 instanceof UDData) {
                            data = ((UDData) param2).bytes();
                        } else if (param2 instanceof LuaString) {
                            data = ((LuaString) param2).m_bytes;
                        } else if (param2 instanceof CharSequence) {
                            data = param2.toString().getBytes();
                        }

                    } else if (param1 instanceof UDData) {
                        data = ((UDData) param1).bytes();
                        name = param2.optjstring(null);
                    }

                    if (data != null && data.length > 0 && !TextUtils.isEmpty(name)) {
                        if (args.isfunction(fixIndex + 3)) {
                            final LuaValue callback = LuaUtil.getFunction(args, fixIndex + 3);
                            new SimpleTask1<Boolean>() {
                                @Override
                                protected Boolean doInBackground(Object... params) {
                                    final String path =
                                            finder.buildSecurePathInSdcard((String) params[0]);
                                    return FileUtil.save(path, (byte[]) params[1]);
                                }

                                @Override
                                protected void onPostExecute(Boolean o) {
                                    LuaUtil.callFunction(callback, o);
                                }
                            }.executeInPool(name, data);
                        } else {
                            final String path = finder.buildSecurePathInSdcard(name);
                            return valueOf(FileUtil.save(path, data));
                        }
                    }
                }
            }
            return FALSE;
        }
    }


    /**
     * 读取文件
     */
    class read extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = fixIndex(args);
            if (args.narg() > fixIndex) {
                final LuaResourceFinder finder = getLuaResourceFinder();
                if (finder != null) {
                    final String name = LuaUtil.getString(args, fixIndex + 1);
                    final String path = finder.buildSecurePathInSdcard(name);

                    if (args.isfunction(fixIndex + 2)) {
                        final LuaValue callback = LuaUtil.getFunction(args, fixIndex + 2);
                        if (path != null) {
                            new SimpleTask1<UDData>() {
                                @Override
                                protected UDData doInBackground(Object... params) {
                                    byte[] data = VenvyFileUtil.readBytes(new File(path));
                                    if (data != null) {
                                        return new UDData(getGlobals(), getmetatable(), null).append(data);
                                    } else {       // 外存储卡读取不到的情况下，尝试在assets资源包下读取
                                        data = VenvyIOUtils.toBytes(VenvyIOUtils.open(App.getContext(),
                                                name));
                                        return new UDData(getGlobals(), getmetatable(), null).append(data);
                                    }
                                }

                                @Override
                                protected void onPostExecute(UDData udData) {
                                    LuaUtil.callFunction(callback, udData);
                                }
                            }.executeInPool();
                        } else {
                            LuaUtil.callFunction(callback, NIL);
                        }
                    } else {
                        if (path != null) {
                            byte[] data = VenvyFileUtil.readBytes(new File(path));
                            if (data != null) {
                                return new UDData(getGlobals(), getmetatable(), null).append(data);
                            } else {       // 外存储卡读取不到的情况下，尝试在assets资源包下读取
                                data = VenvyIOUtils.toBytes(VenvyIOUtils.open(App.getContext(),
                                        name));
                                if (data != null) {
                                    return new UDData(getGlobals(), getmetatable(), null).append(data);
                                } else {
                                    return NIL;
                                }
                            }
                        } else {
                            return NIL;
                        }
                    }
                }
            }
            return NIL;
        }
    }

    /**
     * 文件是否存在
     */
    class exists extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = fixIndex(args);
            if (args.narg() > fixIndex) {
                final String fileName = LuaUtil.getString(args, fixIndex + 1);
                return valueOf(VenvyFileUtil.isExistFile(fileName));
            }
            return valueOf(false);
        }
    }

    /**
     * 获取文件绝对路径
     */
    class path extends VarArgFunction {
        @Override
        public Varargs invoke(Varargs args) {
            final int fixIndex = fixIndex(args);
            if (args.narg() > fixIndex) {
                final LuaResourceFinder finder = getLuaResourceFinder();
                if (finder != null) {
                    final String fileName = LuaUtil.getString(args, fixIndex + 1);
                    final String path = finder.buildSecurePathInSdcard(fileName);
                    return path != null ? valueOf(path) : NIL;
                }
            }
            return NIL;
        }
    }
}
