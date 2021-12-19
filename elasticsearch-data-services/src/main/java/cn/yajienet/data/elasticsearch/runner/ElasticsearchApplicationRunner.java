package cn.yajienet.data.elasticsearch.runner;

import cn.yajienet.data.elasticsearch.init.ElasticSearchInit;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/12/18
 * @Version 1.0.0
 * @Description 初始化 spring boot 后，运行其他任务
 */
@Slf4j
@Component
public class ElasticsearchApplicationRunner implements ApplicationRunner {

    private final ElasticSearchInit elasticSearchInit;

    public ElasticsearchApplicationRunner(ElasticSearchInit elasticSearchInit) {
        this.elasticSearchInit = elasticSearchInit;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        elasticSearchInit.init();

    }


}
