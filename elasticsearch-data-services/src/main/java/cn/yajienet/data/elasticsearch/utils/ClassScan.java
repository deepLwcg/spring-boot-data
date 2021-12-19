package cn.yajienet.data.elasticsearch.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.SystemPropertyUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class ClassScan implements ResourceLoaderAware {

    /**
     * 保存过滤规则要排除的注解
     */
    private final List<TypeFilter> includeFilters = new LinkedList<>();
    private final List<TypeFilter> excludeFilters = new LinkedList<>();

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory( this.resourcePatternResolver );

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils
                .getResourcePatternResolver( resourceLoader );
        this.metadataReaderFactory = new CachingMetadataReaderFactory(
                resourceLoader );
    }

    @SafeVarargs
    public static Set<Class<?>> scan(String[] basePackages, Class<? extends Annotation>... annotations) {
        ClassScan cs = new ClassScan();
        if (ArrayUtils.isNotEmpty( annotations )) {
            for (Class<? extends Annotation> annotation : annotations) {
                cs.addIncludeFilter( new AnnotationTypeFilter( annotation ) );
            }
        }
        Set<Class<?>> classes = new HashSet<>();
        for (String s : basePackages) {
            try {
                classes.addAll( cs.doScan( s ) );
            }catch (Exception e){
                log.warn( e.getLocalizedMessage() );
            }
        }
        return classes;
    }

    public final ResourceLoader getResourceLoader() {
        return this.resourcePatternResolver;
    }

    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add( includeFilter );
    }

    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add( 0, excludeFilter );
    }

    public void resetFilters() {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }

    protected boolean matches(MetadataReader metadataReader) throws IOException {
        for (TypeFilter tf : this.excludeFilters) {
            if (tf.match( metadataReader, this.metadataReaderFactory )) {
                return false;
            }
        }
        for (TypeFilter tf : this.includeFilters) {
            if (tf.match( metadataReader, this.metadataReaderFactory )) {
                return true;
            }
        }
        return false;
    }

    public Set<Class<?>> doScan(String basePackage) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + org.springframework.util.ClassUtils
                    .convertClassNameToResourcePath( SystemPropertyUtils
                            .resolvePlaceholders( basePackage ) )
                    + "/**/*.class";
            Resource[] resources = this.resourcePatternResolver.getResources( packageSearchPath );
            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader( resource );
                    boolean type = (includeFilters.isEmpty() && excludeFilters.isEmpty()) || matches( metadataReader );
                    if (type) {
                        try {
                            classes.add( Class.forName( metadataReader
                                    .getClassMetadata().getClassName() ) );
                        } catch (ClassNotFoundException e) {
                            log.error( "class scan {} is error.", basePackage, e );
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new BeanDefinitionStoreException(
                    "I/O failure during classpath scanning", ex );
        }
        return classes;
    }
}
