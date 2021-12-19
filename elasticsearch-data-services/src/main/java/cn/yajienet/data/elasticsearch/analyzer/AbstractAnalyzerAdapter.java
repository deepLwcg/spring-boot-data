package cn.yajienet.data.elasticsearch.analyzer;

import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/22
 * @Version 1.0.0
 * @Description
 */
public abstract class AbstractAnalyzerAdapter implements Analyzer{

    @Override
    public void builderAnalyzer(XContentBuilder builder) throws IOException {

    }

    @Override
    public String getTokenizerName() {
        return null;
    }

    @Override
    public void builderTokenizer(XContentBuilder builder) throws IOException {

    }

    @Override
    public String getCharFilterName() {
        return null;
    }

    @Override
    public void builderCharFilter(XContentBuilder builder) throws IOException {

    }

    @Override
    public String getFilterName() {
        return null;
    }

    @Override
    public void builderFilter(XContentBuilder builder) throws IOException {

    }
}
