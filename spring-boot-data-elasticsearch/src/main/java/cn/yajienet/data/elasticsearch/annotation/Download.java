package cn.yajienet.data.elasticsearch.annotation;

import cn.yajienet.data.elasticsearch.download.DefaultRemoteDownload;
import cn.yajienet.data.elasticsearch.download.RemoteDownload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description  配置下载器
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Download {

    int maxThreads() default 10;

    /**
     * 默认下载器
     */
    Class<? extends RemoteDownload> remote() default DefaultRemoteDownload.class;

    /**
     * 默认更新器
     */
    Class<? extends RemoteDownload> updateRemote() default DefaultRemoteDownload.class;

}
