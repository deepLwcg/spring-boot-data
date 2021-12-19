package cn.yajienet.data.elasticsearch.data;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.web.client.RestTemplate;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
public interface LoadData {


    /**
     * 获取文档类型的实体Class
     *
     * @return java.lang.Class<?>
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    Class<?> getDocumentClass();

    /**
     * 获取拉取数据初始URL
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/24
     */
    String remoteUri();

    /**
     * 获取拉取更新数据URL
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/11/21
     */
    String updateUri();

    /**
     * 加载数据
     *
     * @param json 数据
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void load(String json);

    /**
     * 配置客户端
     *
     * @param client {@link RestHighLevelClient}
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void setClient(RestHighLevelClient client);

    /**
     * 初始化RestTemplate
     *
     * @param restTemplate {@link RestTemplate}
     * @author Wang Chenguang
     * @date 2021/10/24
     */
    void setRestTemplate(RestTemplate restTemplate);
}
