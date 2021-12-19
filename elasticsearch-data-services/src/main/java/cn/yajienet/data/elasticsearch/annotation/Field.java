package cn.yajienet.data.elasticsearch.annotation;

import cn.yajienet.data.elasticsearch.analyzer.Analyzer;
import cn.yajienet.data.elasticsearch.analyzer.DefaultAnalyzer;
import cn.yajienet.data.elasticsearch.enums.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description  elasticsearch数据类型
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

    FieldType type() default FieldType.Keyword;

    boolean index() default true;

    Class<? extends Analyzer> analyzer() default DefaultAnalyzer.class;

    Class<? extends Analyzer> searchAnalyzer() default DefaultAnalyzer.class;

}
