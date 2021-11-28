package cn.yajienet.data.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/17
 * @Version 1.0.0
 * @Description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadOrder {

    /**
     * 设置运行顺序
     *
     * @return int
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    int value() default 0;

}
