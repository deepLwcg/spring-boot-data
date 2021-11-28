package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.download.AbstractRemoteDownloadAdapter;
import cn.yajienet.data.elasticsearch.download.DownEntity;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class VideosRemoteDownload extends AbstractRemoteDownloadAdapter {
    @Override
    public List<DownEntity> parseUrls(String uri) {
        List<DownEntity> entities = new ArrayList<>();
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity( uri, String.class );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String result = responseEntity.getBody();
            assert result != null;
            result = result.replaceAll( "<br>", "" ).replaceAll( "&", "&amp;" );
            SAXReader reader = new SAXReader();
            try {
                Document document = reader.read( new ByteArrayInputStream( result.getBytes( StandardCharsets.UTF_8 ) ) );
                Element root = document.getRootElement();
                Element list = root.element( "list" );
                if (list != null) {
                    int page = Integer.parseInt( list.attributeValue( "page" ) );
                    int pageCount = Integer.parseInt( list.attributeValue( "pagecount" ) );
                    int pageSize = Integer.parseInt( list.attributeValue( "pagesize" ) );
                    int recordCount = Integer.parseInt( list.attributeValue( "recordcount" ) );
                    for (int i = page; i <= pageCount; i++) {
                        String url = uri + "?ac=videolist&pg=" + i;
                        entities.add( new VideosDownEntity( url ) );
                    }
                }
            } catch (Exception e) {
                log.error( "入口URL解析错误......", e );
            }
        }
        return entities;
    }

    @Override
    public String download(DownEntity entity) {
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity( entity.uri(), String.class );
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }
        return null;
    }

    public static class VideosDownEntity implements DownEntity {

        private final String uri;

        public VideosDownEntity(String uri) {
            this.uri = uri;
        }

        @Override
        public String uri() {
            return this.uri;
        }
    }
}
