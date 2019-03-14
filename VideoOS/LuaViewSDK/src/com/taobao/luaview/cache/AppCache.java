

package com.taobao.luaview.cache;

import android.support.v4.util.LruCache;

import java.util.HashMap;
import java.util.Map;

public class AppCache {
    public static String CACHE_METHODS = "cache_methods";//方法名称缓存
    public static String CACHE_PUBLIC_KEY = "cache_public_key";//公钥
    public static String CACHE_METATABLES = "cache_metatables";//metatable缓存
    public static String CACHE_SCRIPTS = "cache_scripts";//脚本文件缓存，占用空间2%
    public static String CACHE_PROTOTYPE = "cache_prototype";//lua中间码缓存，占用空间3%

    //全局静态cache
    static Map<String, AppCache> mCachePool;

    //simple cache
    Map<Object, Object> mCache;

    //lru cache
    LuaLruCache mLruCache;

    AppCache(int size) {
        mCache = new HashMap<>();
        if (size > 0) {
            mLruCache = new LuaLruCache(size);
        }
    }


    public static AppCache getPrototpyeCache() {
        return getCache(CACHE_PROTOTYPE, (int) (1.5 * 1024 * 1024));
    }

    public static AppCache getCache(String cacheName) {
        return getCache(cacheName, 5);
    }

    public static AppCache getCache(String cacheName, int size) {
        if (mCachePool == null) {
            mCachePool = new HashMap<>();
        }
        if (!mCachePool.containsKey(cacheName)) {
            AppCache appCache = new AppCache(size);
            mCachePool.put(cacheName, appCache);
            return appCache;
        }
        return mCachePool.get(cacheName);
    }


    /**
     * should call when LuaView is destroy
     */
    public static void clear() {
        if (mCachePool != null) {
            mCachePool.clear();
        }
    }


    public static void clear(String... keys) {
        if (mCachePool != null && keys != null) {
            AppCache appCache;
            for (String key : keys) {
                if (mCachePool.containsKey(key)) {
                    appCache = mCachePool.remove(key);
                    if (appCache != null) {
                        if (appCache.mCache != null) {
                            appCache.mCache.clear();
                        }
                        if (appCache.mLruCache != null) {
                            appCache.mLruCache.evictAll();
                        }
                    }
                }
            }
        }
    }

    /**
     * get from cache
     */
    public <T> T get(Object key) {
        if (mCache != null && mCache.get(key) != null) {
            return (T) mCache.get(key);
        }
        return null;
    }

    /**
     * get from cache
     */
    public <T> T getLru(Object key) {
        if (mLruCache != null && mLruCache.get(key) != null) {
            return (T) mLruCache.getWrap(key);
        }
        return null;
    }

    /**
     * update cache
     */
    public <T> T put(Object key, T value) {
        if (mCache != null) {
            mCache.put(key, value);
        }
        return value;
    }

    /**
     * update cache
     */
    public <T> T putLru(Object key, T value, Integer size) {
        if (mLruCache != null) {
            mLruCache.putWrap(key, value, size);
        }
        return value;
    }

    public <T> T putLru(Object key, T value) {
        if (mLruCache != null) {
            mLruCache.putWrap(key, value, null);
        }
        return value;
    }

    //----------------------------------------extend lru object-------------------------------------
    static class LuaLruCache extends LruCache<Object, Object> {
        public LuaLruCache(int maxSize) {
            super(maxSize);
        }

        public void putWrap(Object key, Object value, Integer size) {
            if (key != null && value != null) {
                if (size != null) {
                    super.put(key, new WrapLruObject(value, size));
                } else {
                    super.put(key, value);
                }
            }
        }

        public Object getWrap(Object key) {
            if (key != null) {
                Object result = super.get(key);
                if (result instanceof WrapLruObject) {
                    return ((WrapLruObject) result).obj;
                } else {
                    return result;
                }
            } else {
                return null;
            }
        }

        @Override
        protected int sizeOf(Object key, Object value) {
            if (value instanceof WrapLruObject) {
                return ((WrapLruObject) value).size;
            }
            return super.sizeOf(key, value);
        }
    }

    static class WrapLruObject {
        Object obj;
        int size;

        WrapLruObject(Object obj, int size) {
            this.obj = obj;
            this.size = size;
        }
    }
}
