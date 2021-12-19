package cn.yajienet.data.elasticsearch.download;

import org.springframework.web.client.RestTemplate;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
public abstract class AbstractRemoteDownloadAdapter implements RemoteDownload{

    protected RestTemplate restTemplate;

    @Override
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
