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
public class EnglishAnalyzer extends AbstractAnalyzerAdapter {

    @Override
    public String getName() {
        return "english_analyzer";
    }

    @Override
    public void builderAnalyzer(XContentBuilder builder) throws IOException {
        builder.field( "type","standard" );
        builder.field( "max_token_length",5 );
        builder.field( "stopwords", "_english_");
    }

    @Override
    public String getTokenizerName() {
        return "split_on_non_word";
    }

    @Override
    public void builderTokenizer(XContentBuilder builder) throws IOException {
        builder.field( "type","pattern" );
        builder.field( "pattern", "\\W+");
    }

    @Override
    public String getCharFilterName() {
        return "emoticons";
    }

    @Override
    public void builderCharFilter(XContentBuilder builder) throws IOException {
        builder.field( "type","mapping" );
        builder.startArray("mappings");
        builder.value(  ":) => _happy_");
        builder.value( ":( => _sad_" );
        builder.endArray();
    }

    @Override
    public String getFilterName() {
        return null;
    }

}
