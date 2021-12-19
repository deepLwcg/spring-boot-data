package cn.yajienet.data.elasticsearch.analyzer;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/21
 * @Version 1.0.0
 * @Description
 */
public class DefaultAnalyzer extends AbstractAnalyzerAdapter {

    @Override
    public String getName() {
        return "standard";
    }


}
