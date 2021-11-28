package cn.yajienet.data.elasticsearch.config;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.EnableElasticSearchServer;
import cn.yajienet.data.elasticsearch.context.ElasticSearchContext;
import cn.yajienet.data.elasticsearch.utils.ClassScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class ElasticSearchServerImport implements ImportSelector, BeanFactoryAware {

    private BeanFactory beanFactory;


    protected Class<?> getAnnotationClass() {
        return EnableElasticSearchServer.class;
    }

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes attributes = getAttributes( importingClassMetadata );
        String[] basePackages = attributes.getStringArray( "basePackages" );
        if (basePackages.length == 0) {
            Map<String, Object> beans = getBeanFactory().getBeansWithAnnotation( EnableElasticSearchServer.class );
            for (Map.Entry<String, Object> entry : beans.entrySet()) {
                basePackages = new String[]{entry.getValue().getClass().getPackage().getName()};
                break;
            }
        }

        Set<Class<?>> classSet = ClassScan.scan( basePackages, Document.class );
        classSet.forEach( aClass -> log.info( "扫描到 ElasticSearch 的文档实体: {}", aClass.getName() ));
        ElasticSearchContext.addDocumentsClassAll( classSet );

        return new String[]{ElasticSearchConfiguration.class.getName()};
    }

    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        String name = getAnnotationClass().getName();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap( metadata.getAnnotationAttributes( name, true ) );
        Assert.notNull( attributes, () -> "No auto-configuration attributes found. Is " + metadata.getClassName()
                + " annotated with " + ClassUtils.getShortName( name ) + "?" );
        return attributes;
    }


    public DefaultListableBeanFactory getBeanFactory() {
        return (DefaultListableBeanFactory) beanFactory;
    }
}
