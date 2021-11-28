package cn.yajienet.data.elasticsearch.download;

import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
public interface RemoteDownload {

    /**
     * 配置默认下载工具
     *
     * @param restTemplate {@link RestTemplate}
     * @author Wang Chenguang
     * @date 2021/10/24
     */
    void setRestTemplate(RestTemplate restTemplate);

    /**
     * 获取所有URL
     *
     * @param uri 初始Uri
     * @return java.util.List<cn.yajienet.data.elasticsearch.download.DownEntity>
     * @author Wang Chenguang
     * @date 2021/10/24
     */
    List<DownEntity> parseUrls(String uri);

    /**
     * 下数据
     *
     * @param entity {@link DownEntity}
     * @return java.lang.String
     * @author Wang Chenguang
     * @date 2021/10/24
     */
    String download(DownEntity entity);

}
