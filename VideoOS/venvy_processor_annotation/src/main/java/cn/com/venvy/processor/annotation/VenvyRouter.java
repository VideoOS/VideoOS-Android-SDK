package cn.com.venvy.processor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yanjiangbo on 2018/1/19.
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface VenvyRouter {

    String name();

    String path() default "";

    /**
     * 1 activity
     * 2 view
     * 3 service
     * 4 object
     *
     * @return
     */
    int type() default -1;
}
