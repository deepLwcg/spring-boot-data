package cn.yajienet.data.elasticsearch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/11/13
 * @Version 1.0.0
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch.data")
public class ElasticSearchProperties {

    private List<String> documentScanPackages;

    public List<String> getDocumentScanPackages() {
        return documentScanPackages;
    }

    public void setDocumentScanPackages(List<String> documentScanPackages) {
        this.documentScanPackages = documentScanPackages;
    }
}
