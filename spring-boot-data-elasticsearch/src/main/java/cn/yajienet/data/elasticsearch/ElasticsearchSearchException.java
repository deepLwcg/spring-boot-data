package cn.yajienet.data.elasticsearch;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
public class ElasticsearchSearchException extends RuntimeException  {

    public ElasticsearchSearchException() {
    }

    public ElasticsearchSearchException(String message) {
        super(message);
    }

    public ElasticsearchSearchException(String message, Throwable cause) {
        super(message, cause);
    }

}
