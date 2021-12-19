package cn.yajienet.data.elasticsearch.exception;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
public class ElasticSearchClientException extends RuntimeException  {

    public ElasticSearchClientException() {
    }

    public ElasticSearchClientException(String message) {
        super(message);
    }

    public ElasticSearchClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
