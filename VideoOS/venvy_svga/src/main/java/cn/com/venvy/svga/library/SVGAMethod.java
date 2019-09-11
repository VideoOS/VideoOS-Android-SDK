package cn.com.venvy.svga.library;

/**
 * Created by yanjiangbo on 2018/3/29.
 */

public interface SVGAMethod<T, M, N> {
    N call(T t, M m);
}
