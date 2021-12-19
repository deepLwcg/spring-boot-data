package cn.yajienet.data.elasticsearch.mapping;

import cn.yajienet.data.elasticsearch.analyzer.Analyzer;
import cn.yajienet.data.elasticsearch.analyzer.DefaultAnalyzer;
import cn.yajienet.data.elasticsearch.annotation.Field;
import cn.yajienet.data.elasticsearch.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;


/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class AutoMapping extends AbstractMappingAdapter {

    @Override
    protected void builderObject() throws IOException {
        java.lang.reflect.Field[] fields = documentClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            Field fieldAnnotation = field.getAnnotation( Field.class );
            if (Objects.nonNull( fieldAnnotation )) {
                this.startObject( this.getFieldName( field ) );
                this.builderFieldType( field, fieldAnnotation );
                this.endObject();
            }
        }
    }

    protected void builderFieldType(java.lang.reflect.Field field, Field annotation) throws IOException {
        // 设置字段属性
        this.xContentBuilder.field( "type", annotation.type().name().toLowerCase() );
        // 设置是否创建索引
        if (!annotation.index()) {
            this.xContentBuilder.field( "index", false );
        }
        // 处理日期格式
        builderDataFormat( field, annotation );
        // 处理分词
        builderAnalyzer( field, annotation );

    }

    /**
     * 构造日期格式
     *
     * @param field      {@link java.lang.reflect.Field}
     * @param annotation {@link Field}
     * @author Wang Chenguang
     * @date 2021/10/21
     */
    protected void builderDataFormat(java.lang.reflect.Field field, Field annotation) throws IOException {
        if (FieldType.Date.equals( annotation.type() )) {
            JsonFormat jsonFormat = field.getAnnotation( JsonFormat.class );
            if (Objects.nonNull( jsonFormat )) {
                String pattern = jsonFormat.pattern();
                if (StringUtils.hasLength( pattern )) {
                    this.xContentBuilder.field( "format", pattern );
                }
            }
        }
    }

    /**
     * 构造分词器
     *
     * @param field      {@link java.lang.reflect.Field}
     * @param annotation {@link Field}
     * @author Wang Chenguang
     * @date 2021/10/21
     */
    protected void builderAnalyzer(java.lang.reflect.Field field, Field annotation) {
        if (FieldType.Text.equals( annotation.type() )) {
            try {
                Analyzer analyzer = annotation.analyzer().newInstance();
                if (!(analyzer instanceof DefaultAnalyzer)){
                    if (StringUtils.hasLength( analyzer.getName() )) {
                        this.xContentBuilder.field( "analyzer", analyzer.getName() );
                    }
                }
            } catch (Exception e) {
                log.info( "文档中字段[{}]分词器初始化错误......", field.getName(), e );
            }
            // 处理检索分词
            try {
                Analyzer analyzer = annotation.searchAnalyzer().newInstance();
                if (!(analyzer instanceof DefaultAnalyzer)){
                    if (StringUtils.hasLength( analyzer.getName() )) {
                        this.xContentBuilder.field( "search_analyzer", analyzer.getName() );
                    }
                }
            } catch (Exception e) {
                log.info( "文档中字段[{}]检索分词器初始化错误......", field.getName(), e );
            }
        }
    }


}
