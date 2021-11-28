package cn.yajienet.test.data;

import cn.yajienet.data.elasticsearch.annotation.EnableElasticSearchServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description ElasticSearch TEST
 */
@EnableElasticSearchServer
@SpringBootApplication
public class SpringBootDataTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDataTestApplication.class, args);
    }

}
