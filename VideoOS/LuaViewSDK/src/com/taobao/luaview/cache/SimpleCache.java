
package com.taobao.luaview.cache;

import java.util.HashMap;
import java.util.Map;

public class SimpleCache {
    //全局静态cache
     static Map<String, SimpleCache> mCachePool;

     Map<Object, Object> mCache;

     SimpleCache() {
        mCache = new HashMap<>();
    }

    /**
     * get a named cache
     *
     * @param cacheName
     * @return
     */
    public static SimpleCache getCache(String cacheName) {
        if (mCachePool == null) {
            mCachePool = new HashMap<>();
        }
        if (!mCachePool.containsKey(cacheName)) {
             SimpleCache weakReferenceCache = new SimpleCache();
            mCachePool.put(cacheName, weakReferenceCache);
            return weakReferenceCache;
        }
        return mCachePool.get(cacheName);
    }

    /**
     * should call when LuaView is destroy
     */
    public static void clear() {
        if (mCachePool != null) {
            SimpleCache sc;
            Object cacheObj;
            for (String key : mCachePool.keySet()) {
                sc = mCachePool.get(key);
                if (sc != null && sc.mCache != null) {
                    for (Object key2 : sc.mCache.keySet()) {
                        cacheObj = sc.mCache.get(key2);
                        if (cacheObj instanceof LuaCache.CacheableObject) {
                            ((LuaCache.CacheableObject) cacheObj).onCacheClear();
                        }
                    }
                    sc.mCache.clear();
                }
            }
            mCachePool.clear();
        }
    }

    /**
     * get from cache
     */
    public <T> T get( Object key) {
        if (mCache != null && mCache.get(key) != null) {
            return (T) mCache.get(key);
        }
        return null;
    }

    /**
     * update cache
     */
    public <T> T put(final Object key, T value) {
        if (mCache != null) {
            mCache.put(key, value);
        }
        return value;
    }
}
