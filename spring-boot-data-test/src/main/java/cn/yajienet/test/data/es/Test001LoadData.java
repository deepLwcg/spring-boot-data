package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.annotation.Download;
import cn.yajienet.data.elasticsearch.data.AbstractLoadDataAdapter;
import cn.yajienet.data.elasticsearch.download.DefaultRemoteDownload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
@Component
@Download(remote = DefaultRemoteDownload.class)
public class Test001LoadData extends AbstractLoadDataAdapter<Test001> {

    @Override
    public String remoteUri() {
        return "https://www.baidu.com";
    }

    @Override
    public void load(String json) {
        log.info( "Test001初始化数据:{}", Objects.nonNull( this.restTemplate ) );
        log.info( "{}", json.length() );

    }


}
