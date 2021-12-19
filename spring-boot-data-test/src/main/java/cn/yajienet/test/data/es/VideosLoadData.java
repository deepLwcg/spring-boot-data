package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.annotation.Download;
import cn.yajienet.data.elasticsearch.annotation.LoadOrder;
import cn.yajienet.data.elasticsearch.data.AbstractLoadDataAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Component
@Slf4j
@LoadOrder(1)
@Download(remote = VideosRemoteDownload.class)
public class VideosLoadData extends AbstractLoadDataAdapter<Videos> {

    private final ObjectMapper objectMapper;

    public VideosLoadData(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String remoteUri() {
        return "https://www.88zyw.net/inc/api.php";
    }

    @Override
    public void load(String json) {
        if (StringUtils.isEmpty( json )) {
            return;
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null){
            HttpServletRequest request = ((ServletWebRequest)requestAttributes).getRequest();
            HttpSession session = request.getSession();

        }


        List<String> dataList = new ArrayList<>();
        SAXReader reader = new SAXReader();
        String result = json.replace( "<br>", "" ).replace( "&", "&amp;" );
        try {
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document document = reader.read( new ByteArrayInputStream( result.getBytes( StandardCharsets.UTF_8 ) ) );
            Element root = document.getRootElement();
            Element list = root.element( "list" );
            if (Objects.nonNull( list )) {
                List<Element> videosElement = list.elements( "video" );
                for (Element element : videosElement) {
                    Videos.VideosBuilder builder = Videos.builder();
                    builder.id( element.elementText( "id" ) );
                    builder.tid( element.elementText( "tid" ) );
                    builder.name( element.elementText( "name" ).trim() );
                    builder.type( element.elementText( "type" ) );
                    builder.pic( element.elementText( "pic" ) );
                    builder.lang( element.elementText( "lang" ) );
                    builder.area( element.elementText( "area" ) );
                    builder.year( element.elementText( "year" ) );
                    builder.state( element.elementText( "state" ) );
                    builder.note( element.elementText( "note" ) );
                    builder.actor( element.elementText( "actor" ) );
                    builder.director( element.elementText( "director" ) );
                    builder.des( element.elementText( "des" ) );
                    builder.last( element.elementText( "last" ) );
                    List<Videos.VideoAddr> videoAddrs = new ArrayList<>();
                    List<Element> dls = element.element( "dl" ).elements();
                    if (!CollectionUtils.isEmpty( dls )) {
                        for (Element dd : dls) {
                            String addr = dd.getText();
                            String flag = dd.attributeValue( "flag" ).trim();
                            videoAddrs.add( new Videos.VideoAddr( flag, addr ) );
                        }
                    }
                    builder.videoAddrs( videoAddrs );
                    Videos videos = builder.build();
                    try {
                        String videoJson = objectMapper.writeValueAsString( videos );
                        dataList.add( videoJson );
                    } catch (JsonProcessingException e) {
                        log.info( "load data video to json error" );
                    }
                }
            }
        } catch (DocumentException | SAXException e) {
            log.info( "load data error" );
        }
        this.save( dataList );
    }
}
