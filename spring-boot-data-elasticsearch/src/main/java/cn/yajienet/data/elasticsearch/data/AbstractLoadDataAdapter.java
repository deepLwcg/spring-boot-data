package cn.yajienet.data.elasticsearch.data;

import cn.yajienet.data.elasticsearch.annotation.Document;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public abstract class AbstractLoadDataAdapter<T> implements LoadData {


    protected RestTemplate restTemplate;

    protected RestHighLevelClient client;

    @Override
    public Class<?> getDocumentClass() {
        Type type = this.getClass().getGenericSuperclass();
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] types = parameterizedType.getActualTypeArguments();
        return (Class<?>) types[0];
    }


    @Override
    public String remoteUri() {
        return null;
    }

    @Override
    public String updateUri() {
        return null;
    }

    @Override
    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 保存文档
     *
     * @param documents 所有文档
     * @author Wang Chenguang
     * @date 2021/11/14
     */
    protected void save(List<String> documents) {
        if (CollectionUtils.isEmpty( documents )) {
            return;
        }
        Document documentAnnotation = this.getDocumentClass().getAnnotation( Document.class );
        BulkRequest request = new BulkRequest();
        for (String document : documents) {
            IndexRequest indexRequest = new IndexRequest( documentAnnotation.index() );
            indexRequest.source( document, XContentType.JSON );
            request.add( indexRequest );
        }
        try {
            if (request.numberOfActions() > 0) {
                BulkResponse bulkResponse = this.client.bulk( request, RequestOptions.DEFAULT );
                if (Objects.nonNull( bulkResponse )) {
                    for (BulkItemResponse bulkItemResponse : bulkResponse) {
                        DocWriteResponse itemResponse = bulkItemResponse.getResponse();
                        if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                                || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                            IndexResponse indexResponse = (IndexResponse) itemResponse;
                            log.info( "文档[{}]新增数据成功: {}", documentAnnotation.index(), indexResponse.toString() );
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error( "此文档[{}]加载数据成功，但存入elasticsearch时发生错误......", documentAnnotation.index(), e );
        }

    }

}
