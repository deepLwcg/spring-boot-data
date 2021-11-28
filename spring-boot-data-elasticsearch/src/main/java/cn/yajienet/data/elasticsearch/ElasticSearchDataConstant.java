package cn.yajienet.data.elasticsearch;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/11/13
 * @Version 1.0.0
 * @Description 常量
 */

public class ElasticSearchDataConstant {

    private ElasticSearchDataConstant(){}

    /**
     * 默认加载数据的等级
     */
    public static final int DEFAULT_LOAD_DATA_ORDER = 0;

    /**
     * elasticsearch data run mode is init
     */
    public static final String RUN_MODE_INIT = "init";

    /**
     * elasticsearch data run mode is update
     */
    public static final String RUN_MODE_UPDATE = "update";

    /**
     * elasticsearch data run mode is delete
     */
    public static final String RUN_MODE_DELETE = "delete";

    /**
     * elasticsearch data run mode is none
     */
    public static final String RUN_MODE_NONE = "none";


}
