package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.Field;
import cn.yajienet.data.elasticsearch.enums.FieldType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document(index = "danmoke-videos",alias = "videos")
public class Videos {

    @Field(type = FieldType.Keyword)
    @JsonProperty("id")
    private String id;

    @Field(type = FieldType.Keyword)
    @JsonProperty("tid")
    private String tid;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    @JsonProperty("type")
    private String type;

    @Field(type = FieldType.Keyword)
    @JsonProperty("pic")
    private String pic;

    @Field(type = FieldType.Text)
    @JsonProperty("lang")
    private String lang;

    @Field(type = FieldType.Text)
    @JsonProperty("area")
    private String area;

    @Field(type = FieldType.Keyword)
    @JsonProperty("year")
    private String year;

    @Field(type = FieldType.Keyword)
    @JsonProperty("state")
    private String state;

    @Field(type = FieldType.Keyword)
    @JsonProperty("note")
    private String note;

    @Field(type = FieldType.Text)
    @JsonProperty("actor")
    private String actor;

    @Field(type = FieldType.Text)
    @JsonProperty("director")
    private String director;

    @Field(type = FieldType.Text)
    @JsonProperty("des")
    private String des;

    @Field(type = FieldType.Keyword)
    @JsonProperty("last")
    private String last;

    @JsonProperty("video_addrs")
    private List<VideoAddr> videoAddrs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoAddr{

        @JsonProperty("flag")
        private String flag;

        @JsonProperty("addr")
        private String addr;
    }

    public static class VideosBuilder{


        public static VideosBuilder builder(){
            return  new VideosBuilder();

        }

    }

}
