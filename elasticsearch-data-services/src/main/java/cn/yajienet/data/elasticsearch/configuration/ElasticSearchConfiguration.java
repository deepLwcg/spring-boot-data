package cn.yajienet.data.elasticsearch.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
@ComponentScan(basePackages = "cn.yajienet.data.elasticsearch")
@Configuration
public class ElasticSearchConfiguration {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }


}
