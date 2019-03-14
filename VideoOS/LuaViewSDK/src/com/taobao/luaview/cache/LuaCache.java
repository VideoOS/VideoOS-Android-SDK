

package com.taobao.luaview.cache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LuaCache {
    //缓存的数据，需要在退出的时候清空
     Map<Class, List<WeakReference<CacheableObject>>> mCachedObjects;

    //缓存数据管理器
    public interface CacheableObject {
        void onCacheClear();
    }

    /**
     * clear cache
     */
    public static void clear() {
        SimpleCache.clear();
        WeakCache.clear();
    }

    /**
     * 缓存对象
     *

     */
    public void cacheObject(Class type, CacheableObject obj) {
        if (mCachedObjects == null) {
            mCachedObjects = new HashMap<>();
        }
        List<WeakReference<CacheableObject>> cache = mCachedObjects.get(type);
        if (cache == null) {
            cache = new ArrayList<>();
            mCachedObjects.put(type, cache);
        }

        if (!cache.contains(obj)) {
            cache.add(new WeakReference<>(obj));
        }
    }

    /**
     * 清理所有缓存的对象
     * TODO 需要在onShow的时候恢复所有cache后的对象
     */
    public void clearCachedObjects() {
        if (mCachedObjects != null && mCachedObjects.size() > 0) {
            for ( Class type : mCachedObjects.keySet()) {
                List<WeakReference<CacheableObject>> cache = mCachedObjects.get(type);
                if (cache != null) {
                    for (int i = 0; i < cache.size(); i++) {
                         WeakReference<CacheableObject> obj = cache.get(i);
                        if (obj != null && obj.get() != null) {
                            obj.get().onCacheClear();
                        }
                        cache.set(i, null);
                    }
                }
                mCachedObjects.put(type, null);
            }
            mCachedObjects.clear();
        }
        mCachedObjects = null;
    }
}
