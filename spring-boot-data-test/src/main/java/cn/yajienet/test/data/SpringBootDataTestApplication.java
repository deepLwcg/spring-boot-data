package cn.yajienet.test.data;

import cn.yajienet.data.elasticsearch.annotation.EnableElasticSearchServices;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description ElasticSearch TEST
 */
@Slf4j
@EnableElasticSearchServices
@SpringBootApplication
public class SpringBootDataTestApplication {


    @SneakyThrows
    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder( SpringBootDataTestApplication.class );
        springApplicationBuilder.web( WebApplicationType.NONE );
        springApplicationBuilder.run( args );
    }

}
