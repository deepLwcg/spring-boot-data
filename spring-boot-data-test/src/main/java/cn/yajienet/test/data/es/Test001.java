package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.analyzer.EnglishAnalyzer;
import cn.yajienet.data.elasticsearch.analyzer.SimpleAnalyzer;
import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.Field;
import cn.yajienet.data.elasticsearch.enums.FieldType;
import lombok.Data;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Data
@Document(index = "yajienet-test", alias = "test")
public class Test001 {

    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Text,analyzer = EnglishAnalyzer.class)
    private String name;

    @Field(type = FieldType.Double)
    private Integer count;

    @Field(type = FieldType.Text, analyzer = SimpleAnalyzer.class)
    private String test;


}
