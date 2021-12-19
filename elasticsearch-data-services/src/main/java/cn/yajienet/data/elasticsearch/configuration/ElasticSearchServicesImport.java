package cn.yajienet.data.elasticsearch.configuration;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.EnableElasticSearchServices;
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
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/12/18
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class ElasticSearchServicesImport implements ImportSelector, BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @NonNull
    @Override
    public String[] selectImports(@NonNull AnnotationMetadata importingClassMetadata) {
        Set<Class<?>> classSet = new HashSet<>();
        AnnotationAttributes attributes = getAttributes( importingClassMetadata );
        String[] basePackages = attributes.getStringArray( "basePackages" );
        if (basePackages.length > 0) {
            classSet.addAll( ClassScan.scan( basePackages, Document.class ) );
        } else {
            Map<String, Object> beans = getBeanFactory().getBeansWithAnnotation( EnableElasticSearchServices.class );
            if (!CollectionUtils.isEmpty( beans )) {
                String[] beansPackages = new String[]{beans.entrySet().iterator().next().getValue().getClass().getPackage().getName()};
                classSet.addAll( ClassScan.scan( beansPackages, Document.class ) );
            }
        }
        if (!CollectionUtils.isEmpty( classSet )) {
            ElasticSearchContext.addDocumentsClassAll( classSet );
        }
        return new String[]{ElasticSearchConfiguration.class.getName()};
    }

    @Override
    public Predicate<String> getExclusionFilter() {
        return ImportSelector.super.getExclusionFilter();
    }

    protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
        String name = getAnnotationClass().getName();
        AnnotationAttributes attributes = AnnotationAttributes.fromMap( metadata.getAnnotationAttributes( name, true ) );
        Assert.notNull( attributes, () -> "No auto-configuration attributes found. Is " + metadata.getClassName()
                + " annotated with " + ClassUtils.getShortName( name ) + "?" );
        return attributes;
    }

    protected Class<?> getAnnotationClass() {
        return EnableElasticSearchServices.class;
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return (DefaultListableBeanFactory) beanFactory;
    }
}
