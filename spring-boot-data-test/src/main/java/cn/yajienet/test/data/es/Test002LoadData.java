package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.annotation.LoadOrder;
import cn.yajienet.data.elasticsearch.data.AbstractLoadDataAdapter;
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
@LoadOrder(-1)
public class Test002LoadData extends AbstractLoadDataAdapter<Test002> {


    @Override
    public void load(String json) {
        log.info( "Test002初始化数据:{}", Objects.nonNull( this.restTemplate ) );


    }
}
