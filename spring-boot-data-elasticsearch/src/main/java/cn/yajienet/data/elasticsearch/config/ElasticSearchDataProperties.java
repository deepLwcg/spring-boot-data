package cn.yajienet.data.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/11/13
 * @Version 1.0.0
 * @Description
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch.data")
public class ElasticSearchDataProperties {

    private boolean script = false;

    private String type = "none";

    public boolean getScript() {
        return script;
    }

    public void setScript(boolean script) {
        this.script = script;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
