package cn.com.venvy.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yanjiangbo on 2018/1/19.
 */


@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)

public @interface VenvyAutoRun {
}
