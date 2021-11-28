package cn.yajienet.data.elasticsearch.annotation;

import cn.yajienet.data.elasticsearch.document.DefaultInitializeDocument;
import cn.yajienet.data.elasticsearch.document.InitializeDocument;
import cn.yajienet.data.elasticsearch.es.AutoMapping;
import cn.yajienet.data.elasticsearch.es.DefaultSetting;
import cn.yajienet.data.elasticsearch.es.Mapping;
import cn.yajienet.data.elasticsearch.es.Setting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description  elasticsearch 文档
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {

    String index() default "";

    String alias() default "";

    Class<? extends Setting> setting() default DefaultSetting.class;

    Class<? extends Mapping> mapping() default AutoMapping.class;

    Class<? extends InitializeDocument> initializeDocument() default DefaultInitializeDocument.class;

}
