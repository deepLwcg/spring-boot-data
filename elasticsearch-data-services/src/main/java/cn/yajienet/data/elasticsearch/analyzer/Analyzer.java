package cn.yajienet.data.elasticsearch.analyzer;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/12/18
 * @Version 1.0.0
 * @Description
 */
public interface Analyzer {

    /**
     * 分词器的名字
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/21
     */
    String getName();

    /**
     * 构造分词器设置到Setting中
     *
     * @param builder {@link XContentBuilder}
     * @author Wang Chenguang
     * @throws IOException {@link IOException} IO
     * @date 2021/10/21
     */
    void builderAnalyzer(XContentBuilder builder) throws IOException;

    /**
     * tokenizer name
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    String getTokenizerName();

    /**
     * 构建 tokenizer
     *
     * @param builder {@link XContentBuilder}
     * @throws IOException {@link IOException} IO
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    void builderTokenizer(XContentBuilder builder) throws IOException;


    /**
     * char_filter name
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    String getCharFilterName();

    /**
     * 构建 char_filter
     *
     * @param builder {@link XContentBuilder}
     * @throws IOException {@link IOException} IO
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    void builderCharFilter(XContentBuilder builder) throws IOException;

    /**
     * filter name
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    String getFilterName();

    /**
     * 构建 filter
     *
     * @param builder {@link XContentBuilder}
     * @throws IOException {@link IOException} IO
     * @author Wang Chenguang
     * @date 2021/10/22
     */
    void builderFilter(XContentBuilder builder) throws IOException;
}
