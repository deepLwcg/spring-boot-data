package cn.yajienet.data.elasticsearch.setting;

import cn.yajienet.data.elasticsearch.analyzer.*;
import cn.yajienet.data.elasticsearch.annotation.Field;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/18
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public abstract class AbstractSettingAdapter implements Setting {

    protected XContentBuilder xContentBuilder;

    protected Set<Analyzer> analyzers;

    protected Class<?> documentClass;

    @Override
    public void setDocumentClass(Class<?> documentClass) {
        this.documentClass = documentClass;
    }

    @Override
    public XContentBuilder getSetting() {
        this.analyzers = findFieldsAnalyzers();
        try {
            this.jsonBuilder();
            this.builderObject( AbstractSettingAdapter::builderObject );
        } catch (IOException e) {
            log.error( "文档Setting初始化失败......" );
        }
        return this.xContentBuilder;
    }

    /**
     * 构造
     *
     * @throws IOException IO {@link IOException}
     * @author Wang Chenguang
     * @date 2021/10/20
     */
    protected void builderObject() throws IOException {
        this.builderIndex();
        if (!CollectionUtils.isEmpty( this.analyzers )) {
            this.startObject( "analysis" );
            this.builderAnalyzer();
            this.builderTokenizer();
            this.builderFilter();
            this.builderCharFilter();
            this.endObject();
        }
    }

    protected void jsonBuilder() throws IOException {
        this.xContentBuilder = XContentFactory.jsonBuilder();
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

    protected void builderObject(SettingConsumer<AbstractSettingAdapter> consumer) throws IOException {
        this.startObject();
        consumer.accept( this );
        this.endObject();
    }


    /**
     * 查找当前文档所有设置的分词器，设置到setting中
     */
    protected Set<Analyzer> findFieldsAnalyzers() {
        Set<Analyzer> analyzers = new LinkedHashSet<>();
        java.lang.reflect.Field[] fields = this.documentClass.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            Field fieldAnnotation = field.getAnnotation( Field.class );
            if (Objects.isNull( fieldAnnotation )){
                continue;
            }
            try {
                Analyzer analyzer = fieldAnnotation.analyzer().newInstance();
                if (analyzer instanceof DefaultAnalyzer
                        || analyzer instanceof WhitespaceAnalyzer
                        || analyzer instanceof SimpleAnalyzer
                        || analyzer instanceof IkSmartAnalyzer
                        || analyzer instanceof IkMaxWordAnalyzer) {
                    continue;
                }
                analyzers.add( analyzer );
            } catch (InstantiationException | IllegalAccessException e) {
                log.error( "构建Setting时，初始化分词器错误......" );
            }

        }
        return analyzers;
    }

    /**
     * 构造Analyzer
     *
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    protected void builderAnalyzer() throws IOException {
        this.startObject( "analyzer" );
        {
            for (Analyzer analyzer : this.analyzers) {
                String analyzerName = analyzer.getName();
                if (StringUtils.hasLength( analyzerName )) {
                    this.startObject( analyzerName );
                    analyzer.builderAnalyzer( this.xContentBuilder );
                    this.endObject();
                }
            }
        }
        this.endObject();
    }

    /**
     * 构造Tokenizer
     *
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    protected void builderTokenizer() throws IOException {
        this.startObject( "tokenizer" );
        {
            for (Analyzer analyzer : this.analyzers) {
                String tokenizerName = analyzer.getTokenizerName();
                if (StringUtils.hasLength( tokenizerName )) {
                    this.startObject( tokenizerName );
                    analyzer.builderTokenizer( this.xContentBuilder );
                    this.endObject();
                }
            }
        }
        this.endObject();
    }

    /**
     * 构造CharFilter
     *
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    protected void builderCharFilter() throws IOException {
        this.startObject( "char_filter" );
        {
            for (Analyzer analyzer : this.analyzers) {
                String charFilterName = analyzer.getCharFilterName();
                if (StringUtils.hasLength( charFilterName )) {
                    this.startObject( charFilterName );
                    analyzer.builderCharFilter( this.xContentBuilder );
                    this.endObject();
                }
            }
        }
        this.endObject();
    }

    /**
     * 构造Filter
     *
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    protected void builderFilter() throws IOException {
        this.startObject( "filter" );
        {
            for (Analyzer analyzer : this.analyzers) {
                String filterName = analyzer.getFilterName();
                if (StringUtils.hasLength( filterName )) {
                    this.startObject( filterName );
                    analyzer.builderFilter( this.xContentBuilder );
                    this.endObject();
                }
            }
        }
        this.endObject();
    }

    /**
     * 构造头部Index
     *
     * @throws IOException IO {@link IOException}
     * @author Wang Chenguang
     * @date 2021/10/20
     */
    protected abstract void builderIndex() throws IOException;


    /**
     * 抽象接口
     */
    interface SettingConsumer<T extends Setting> {

        /**
         * 构建
         *
         * @param settingAdapter {@link AbstractSettingAdapter}
         * @throws IOException IO {@link IOException}
         * @author Wang Chenguang
         * @date 2021/10/20
         */
        void accept(AbstractSettingAdapter settingAdapter) throws IOException;
    }
}
