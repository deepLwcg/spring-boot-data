package cn.yajienet.data.elasticsearch.document;


import cn.yajienet.data.elasticsearch.mapping.Mapping;
import cn.yajienet.data.elasticsearch.setting.Setting;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
public interface InitializeDocument{


    /**
     * 配置客户端
     *
     * @param client {@link RestHighLevelClient}
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void setClient(RestHighLevelClient client);

    /**
     * 配置对应的文档Class
     *
     * @param documentClass {@link Class}
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void setDocumentClass(Class<?> documentClass);

    /**
     * 配置Setting
     *
     * @param setting {@link Setting}
     * @author Wang Chenguang
     * @date 2021/10/18
     */
    void setSetting(Setting setting);


    /**
     * 配置Mapping
     *
     * @param mapping {@link Mapping}
     * @author Wang Chenguang
     * @date 2021/10/18
     */
    void setMapping(Mapping mapping);

    /**
     * 删除文档
     *
     * @return boolean
     * @author Wang Chenguang
     * @date 2021/10/19
     */
    boolean delete();

    /**
     * 初始化文档
     *
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void initialize();



}
