/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.Iterator;

import cn.com.venvy.common.utils.VenvyLog;

/**
 * Json 处理
 *
 * @author song
 * @date 15/9/6
 */
public class JsonUtil {

    /**
     * convert a lua table to data string
     *
     * @param table
     * @return
     */
    public static String toString(LuaTable table) {
        JSONArray array = null;
        JSONObject object = null;
        Object obj = toJSON(table);
        if (obj == null) {
            return "";
        }
        try {
            if (obj instanceof JSONArray) {
                array = (JSONArray) obj;
            }
            if (obj instanceof JSONObject) {
                object = (JSONObject) obj;
            }
            return object != null ? object.toString(2) : array != null ? array.toString(2) : "";
        } catch (JSONException e) {
            VenvyLog.e(JsonUtil.class.getName(), e);
        }
        return "";
    }

    public static String toString(Object object) {
        if (object instanceof LuaTable) {
            return toString((LuaTable) object);
        }
        return LuaValue.NIL.toString();
    }

    public static Object toJSON(LuaTable table) {
        JSONObject obj = null;
        JSONArray array = null;
        if (table != null) {
            LuaValue[] keys = table.keys();
            if (keys != null && keys.length > 0) {
                try {
                    for (int i = 0; i < keys.length; i++) {
                        LuaValue keyValue = keys[i];
                        LuaValue startKey = keys[0];
                        if (LuaUtil.isNumber(startKey) && startKey.optint(0) == 1) {
                            LuaValue value = table.get(keyValue);
                            if (array == null) {
                                array = new JSONArray();
                            }
                            if (value instanceof LuaTable) {
                                array.put(toJSON((LuaTable) value));
                            } else {
                                array.put(value);
                            }
                        } else {
                            String key = keyValue.optjstring(null);
                            LuaValue value = table.get(keyValue);
                            if (obj == null) {
                                obj = new JSONObject();
                            }
                            if (value.istable()) {
                                obj.put(key, toJSON((LuaTable) value));
                            } else {
                                if (value instanceof LuaBoolean) {
                                    obj.put(key, value.optboolean(false));
                                } else if (value instanceof LuaInteger) {
                                    obj.put(key, value.optint(0));
                                } else if (value instanceof LuaDouble) {
                                    obj.put(key, value.optlong(0L));
                                } else if (value instanceof LuaString) {
                                    obj.put(key, value.optstring(null));
                                } else {
                                    obj.put(key, value);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    VenvyLog.e(JsonUtil.class.getName(), e);
                }
            }
        }
        return obj != null ? obj : array != null ? array : null;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param obj
     * @return
     */
    static LuaValue toLuaTable(JSONObject obj) {
        LuaValue result = LuaValue.NIL;

        if (obj != null) {
            result = new LuaTable();
            if (obj.length() > 0) {//只要不空，就创建一个table
                Iterator<String> iter = obj.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Object value = obj.opt(key);
                    result.set(key, toLuaValue(value));
                }
            }
        }
        return result;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param jsonString
     * @return
     */
    public static LuaValue toLuaTable(String jsonString) {
        LuaValue luaTable = LuaValue.NIL;
        try {
            luaTable = toLuaTable(new JSONObject(jsonString));
        } catch (Exception e) {
            try {
                luaTable = toLuaTable(new JSONArray(jsonString));
            } catch (Exception ex1) {
                VenvyLog.e(JsonUtil.class.getName(), ex1);
            }
        }
        return luaTable;
    }

    /**
     * 判断是否可以转成json
     *
     * @param jsonString
     * @return
     */
    public static boolean isJson(String jsonString) {
        try {
            new JSONObject(jsonString);
        } catch (JSONException ex) {
            try {
                new JSONArray(jsonString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将JSONObject转成LuaTable
     *
     * @param obj
     * @return
     */
    static LuaValue toLuaTable(JSONArray obj) {
        LuaValue result = LuaValue.NIL;

        if (obj != null) {
            result = new LuaTable();//只要不空，就创建一个table
            if (obj.length() > 0) {
                for (int i = 0; i < obj.length(); i++) {
                    int key = i + 1;
                    Object value = obj.opt(i);
                    result.set(key, toLuaValue(value));
                }
            }
        }
        return result;
    }

    /**
     * convert a object to LuaValue
     *
     * @param value
     * @return
     */
    private static LuaValue toLuaValue(Object value) {
        if (value instanceof String) {
            return LuaValue.valueOf((String) value);
        } else if (value instanceof Integer) {
            return LuaValue.valueOf((Integer) value);
        } else if (value instanceof Long) {
            return LuaValue.valueOf((Long) value);
        } else if (value instanceof Double) {
            return LuaValue.valueOf((Double) value);
        } else if (value instanceof Boolean) {
            return LuaValue.valueOf((Boolean) value);
        } else if (value instanceof JSONObject) {
            return toLuaTable((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return toLuaTable((JSONArray) value);
        } else {
            //TODO 不支持的类型
            return LuaValue.NIL;
        }
    }


    public static String[] toStringArray(JSONArray array) throws JSONException {
        if (array == null) return new String[]{};

        String[] args = new String[array.length()];
        for (int i = 0, len = array.length(); i < len; i++) {
            args[i] = String.valueOf(array.get(i));
        }
        return args;
    }
}
