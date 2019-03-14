package cn.com.venvy.common.interf;

import android.support.annotation.NonNull;

/**
 * 数据缓存接口
 * Created by Yanqiu on 17/9/20.
 */

public interface Cache<T> {
    /**
     * 获取缓存
     *
     * @param key
     * @return
     */
    T get(@NonNull String key);

    /**
     * 保存缓存
     *
     * @param key
     * @param entry
     */
    void save(@NonNull String key, @NonNull T entry);

    /**
     * 根据key删除缓存
     *
     * @param key
     */
    void remove(@NonNull String key);

    /**
     * 清空缓存
     */
    void clear();


}
