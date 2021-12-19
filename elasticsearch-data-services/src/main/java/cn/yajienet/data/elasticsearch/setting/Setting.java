package cn.yajienet.data.elasticsearch.setting;

import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
public interface Setting {


    /**
     * 配置对应的文档Class
     *
     * @param documentClass {@link Class}
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    void setDocumentClass(Class<?> documentClass);

    /**
     * 返回Setting
     *
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/16
     */
    XContentBuilder getSetting();

}
