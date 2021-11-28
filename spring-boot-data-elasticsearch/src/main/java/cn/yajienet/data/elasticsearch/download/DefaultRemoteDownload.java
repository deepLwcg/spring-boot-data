package cn.yajienet.data.elasticsearch.download;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description 默认下载器
 */
@Slf4j
public class DefaultRemoteDownload extends AbstractRemoteDownloadAdapter {

    @Override
    public List<DownEntity> parseUrls(String uri) {
        log.info( "解析入口URL: {}", uri );


        List<DownEntity> downEntities = new ArrayList<>();
        downEntities.add( new DefaultDownEntity( "https://www.baidu.com" ) );
        return downEntities;
    }

    @Override
    public String download(DownEntity entity) {
        DefaultDownEntity defaultDownEntity = (DefaultDownEntity) entity;
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity( defaultDownEntity.uri(), String.class );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    public static class DefaultDownEntity implements DownEntity {

        private final String uri;

        public DefaultDownEntity(String uri) {
            this.uri = uri;
        }

        @Override
        public String uri() {
            return this.uri;
        }
    }

}
