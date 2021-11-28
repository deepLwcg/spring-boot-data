package cn.yajienet.data.elasticsearch.document;/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.es.Mapping;
import cn.yajienet.data.elasticsearch.es.Setting;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.rest.RestStatus;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public abstract class AbstractInitializeDocumentAdapter implements InitializeDocument {


    protected RestHighLevelClient client;

    protected Class<?> documentClass;

    protected Setting setting;

    protected Mapping mapping;

    @Override
    public void setClient(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void setDocumentClass(Class<?> documentClass) {
        this.documentClass = documentClass;
    }

    @Override
    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    @Override
    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public boolean delete() {
        Document document = documentClass.getAnnotation( Document.class );
        if (!StringUtils.hasLength( document.index() )) {
            log.error( "[{}]未设置索引(Index)，跳过初始化此文档！", documentClass.getName() );
            return false;
        }
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest( document.index() );
        try {
            AcknowledgedResponse deleteIndexResponse = this.client.indices().delete( deleteIndexRequest, RequestOptions.DEFAULT );
            if (deleteIndexResponse.isAcknowledged()) {
                log.info( "原始文档[{}]已经删除！继续初始化文档......", document.index() );
                return true;
            }
        } catch (ElasticsearchException exception) {
            if (exception.status() == RestStatus.NOT_FOUND) {
                log.info( "[{}]删除的文档不存在......", document.index() );
                return true;
            } else {
                log.error( "原始文档[{}]删除失败！终止初始化文档和数据......", document.index(), exception );
            }
        } catch (IOException e) {
            log.error( "原始文档[{}]删除失败！终止初始化文档和数据......", document.index(), e );
        }
        return false;
    }
}
