package cn.yajienet.data.elasticsearch.setting;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class DefaultSetting extends AbstractSettingAdapter {

    @Override
    protected void builderIndex() throws IOException {
        this.xContentBuilder.field( "index.mapping.depth.limit",50 );
    }

}
