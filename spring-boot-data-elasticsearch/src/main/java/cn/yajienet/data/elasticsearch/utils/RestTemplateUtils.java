package cn.yajienet.data.elasticsearch.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class RestTemplateUtils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36";
    public static final String ACCEPT_LANGUAGE = "zh-CN,zh;q=0.9";

    private final Set<ClientHttpRequestInterceptor> interceptors = new CopyOnWriteArraySet<>();

    private final RestTemplate restTemplate = elasticSearchRest();

    private static class RestTemplateUtilsHolder {
        public static final RestTemplateUtils INSTANCE = new RestTemplateUtils();
    }

    private static RestTemplateUtils getInstance() {
        return RestTemplateUtilsHolder.INSTANCE;
    }

    public RestTemplate elasticSearchRest() {
        // SSL
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register( "http", PlainConnectionSocketFactory.getSocketFactory() )
                .register( "https", sslConnectionSocketFactory() ).build();
        // 连接池
        PoolingHttpClientConnectionManager pollingConnectionManager = poolingHttpClientConnectionManager( registry );
        //使用httpClient创建一个ClientHttpRequestFactory的实现
        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig( requestConfig() )
                .setUserAgent( USER_AGENT )
                .setConnectionManager( pollingConnectionManager )
                .setConnectionManagerShared( true )
                .setKeepAliveStrategy( connectionKeepAliveStrategy() )
                .setDefaultSocketConfig( socketConfig() )
                .build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory( httpClient );
        RestTemplate restTemplateClient = new RestTemplate( requestFactory );
        restTemplateClient.getInterceptors().add( httpRequestInterceptor() );
        return restTemplateClient;
    }


    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(Registry<ConnectionSocketFactory> registry) {
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager( registry );
        //设置整个连接池最大连接数 根据自己的场景决定
        pollingConnectionManager.setMaxTotal( 200 );
        //路由是对maxTotal的细分，设置每个route默认的最大连接数
        pollingConnectionManager.setDefaultMaxPerRoute( 50 );
        //该方法关闭超过连接保持时间的连接，并从池中移除。
        pollingConnectionManager.closeExpiredConnections();
        //该方法关闭空闲时间超过timeout的连接，空闲时间从交还给连接池时开始，不管是否已过期，超过空闲时间则关闭。
        pollingConnectionManager.closeIdleConnections( 10, TimeUnit.SECONDS );
        return pollingConnectionManager;
    }

    public SocketConfig socketConfig() {
        return SocketConfig.custom()
                // 开启监视TCP连接是否有效
                .setSoKeepAlive( false )
                .setSoLinger( 1 )
                // 是否可以在一个进程关闭Socket后，即使它还没有释放端口，其它进程还可以立即重用端口
                .setSoReuseAddress( true )
                // 超时时间设置,接收数据的等待超时时间，单位ms
                .setSoTimeout( 5 * 1000 )
                // 是否立即发送数据，设置为true会关闭Socket缓冲，默认为false
                .setTcpNoDelay( false ).build();
    }

    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        //避免服务器端吊死客户端连接，例如：服务端keepAlive贼长或，client -1 为永远
        return new DefaultConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
                long keepAlive = super.getKeepAliveDuration( response, context );
                return keepAlive == -1 ? 5000 : keepAlive;
            }
        };
    }

    public RequestConfig requestConfig() {
        //生成一个设置了连接超时时间、请求超时时间、异常最大重试次数的httpClient
        return RequestConfig.custom()
                // 设置从connectManager获取Connection 超时时间
                .setConnectionRequestTimeout( 20000 )
                // 设置连接超时时间
                .setConnectTimeout( 50000 )
                // 请求获取数据的超时时间
                .setSocketTimeout( 50000 )
                // 确定循环重定向(重定向到相同位置)是否应该重定向
                .setCircularRedirectsAllowed( false )
                // 重定向的最大数目。对重定向次数的限制是为了防止无限循环
                .setMaxRedirects( 5 )
                // 确定是否应自动处理重定向
                .setRedirectsEnabled( true )
                // 确定是否应拒绝相对重定向。HTTP规范要求位置值是一个绝对URI
                .setRelativeRedirectsAllowed( true )
                // 确定是否应自动解压缩压缩实体
                .setContentCompressionEnabled( true )
                // 返回用于请求执行的本地地址。在具有多个网络接口的计算机上，此参数可用于选择其中的网络接口连接产生。
                // .setLocalAddress()
                // 代理配置
                // .setProxy()
                .build();
    }

    public SSLConnectionSocketFactory sslConnectionSocketFactory() {
        try {
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial( null, acceptingTrustStrategy ).build();
            return new SSLConnectionSocketFactory( sslContext, new NoopHostnameVerifier() );
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            log.error( "Unable to initiate RestTemplate for access wechat-proxy." );
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    public ClientHttpRequestInterceptor httpRequestInterceptor() {
        return (httpRequest, bytes, execution) -> {
            httpRequest.getHeaders().set( HttpHeaders.ACCEPT_LANGUAGE, ACCEPT_LANGUAGE );
            //add Referer  httpRequest.getHeaders().set( "Referer", httpRequest.getURI().toString() )
            httpRequest.getHeaders().set( HttpHeaders.HOST, httpRequest.getURI().getHost() );
            httpRequest.getHeaders().set( HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br" );
            httpRequest.getHeaders().set( HttpHeaders.CONNECTION, "keep-alive" );
            return execution.execute( httpRequest, bytes );
        };
    }

    public static void addInterceptor(ClientHttpRequestInterceptor interceptor) {
        if (!getInstance().interceptors.contains( interceptor )) {
            RestTemplateUtils.restTemplate().getInterceptors().add( interceptor );
            getInstance().interceptors.add( interceptor );
        }
    }

    public static RestTemplate restTemplate() {
        return getInstance().restTemplate;
    }


}
