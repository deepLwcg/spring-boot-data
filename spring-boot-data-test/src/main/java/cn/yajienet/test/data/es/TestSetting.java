package cn.yajienet.test.data.es;

import cn.yajienet.data.elasticsearch.setting.Setting;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/11/14
 * @Version 1.0.0
 * @Description
 */
public class TestSetting implements Setting {


    @Override
    public void setDocumentClass(Class<?> documentClass) {

    }

    @Override
    public XContentBuilder getSetting() {
        return null;
    }
}
