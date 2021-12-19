package cn.yajienet.data.elasticsearch.mapping;

import cn.yajienet.data.elasticsearch.utils.StringEsUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/18
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public abstract class AbstractMappingAdapter implements Mapping {

    protected Class<?> documentClass;

    protected XContentBuilder xContentBuilder;

    @Override
    public void setDocumentClass(Class<?> documentClass) {
        this.documentClass = documentClass;
    }

    @Override
    public XContentBuilder getMapping() {
        try {
            this.jsonBuilder();
            this.startObject();
            this.builderObject( AbstractMappingAdapter::builderObject );
            this.endObject();
            return this.xContentBuilder;
        } catch (IOException e) {
            log.info( "构建Mapping失败......", e );
        }
        return null;
    }

    protected void jsonBuilder() throws IOException {
        this.xContentBuilder = XContentFactory.jsonBuilder();
    }

    protected void builderObject(MappingConsumer<AbstractMappingAdapter> consumer) throws IOException {
        this.startObject( "properties" );
        consumer.accept( this );
        this.xContentBuilder.endObject();
    }

    protected void startObject() throws IOException {
        this.xContentBuilder.startObject();
    }

    protected void startObject(String name) throws IOException {
        this.xContentBuilder.startObject( name );
    }

    protected void endObject() throws IOException {
        this.xContentBuilder.endObject();
    }

    protected String getFieldName(Field field) {
        JsonProperty jsonProperty = field.getAnnotation( JsonProperty.class );
        return (Objects.nonNull( jsonProperty ) ? jsonProperty.value() : StringEsUtils.humpToLine( field.getName() ));
    }

    /**
     * 构造最内部json
     *
     * @throws IOException IO {@link IOException}
     * @author Wang Chenguang
     * @date 2021/10/20
     */
    protected abstract void builderObject() throws IOException;

    /**
     * 抽象接口
     */
    interface MappingConsumer<T extends Mapping> {

        /**
         * 构建
         *
         * @param mappingAdapter {@link AbstractMappingAdapter}
         * @throws IOException IO {@link IOException}
         * @author Wang Chenguang
         * @date 2021/10/20
         */
        void accept(AbstractMappingAdapter mappingAdapter) throws IOException;
    }


}
