package cn.yajienet.data.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEntity {

    /**
     * 文档索引名
     */
    @JsonProperty("index")
    private String index;

    /**
     * 任务需要执行的数据
     */
    @JsonProperty("tasks")
    private List<String> tasks;


    /**
     * 执行顺序
     */
    @JsonProperty("order")
    private Integer order;


}
