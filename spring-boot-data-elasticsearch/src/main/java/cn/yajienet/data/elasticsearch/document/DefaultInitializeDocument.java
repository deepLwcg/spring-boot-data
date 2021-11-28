package cn.yajienet.data.elasticsearch.document;

import cn.yajienet.data.elasticsearch.annotation.Document;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description 默认初始化文档类
 */
@Slf4j
public class DefaultInitializeDocument extends AbstractInitializeDocumentAdapter {

    private CreateIndexRequest indexRequest;


    @Override
    public void initialize() {
        log.info( "开始初始化文档[{}] loading......", documentClass.getName() );
        indexRequestBuilder();
        create();
    }

    /**
     * 构建索引请求
     *
     * @author Wang Chenguang
     * @date 2021/10/19
     */
    protected void indexRequestBuilder() {
        Document document = documentClass.getAnnotation( Document.class );
        String index = document.index();
        String alias = document.alias();
        XContentBuilder setting = this.setting.getSetting();
        XContentBuilder mapping = this.mapping.getMapping();
        this.indexRequest = new CreateIndexRequest( index );
        if (StringUtils.hasLength( alias )) {
            indexRequest.alias( new Alias( alias ) );
        }
        if (Objects.nonNull( setting )) {
            indexRequest.settings( setting );
        }
        if (Objects.nonNull( mapping )) {
            indexRequest.mapping( mapping );
        }
    }

    /**
     * 异步创建文档
     *
     * @author Wang Chenguang
     * @date 2021/10/19
     */
    protected void create() {
        if (Objects.isNull( this.indexRequest )) {
            return;
        }
        String index = this.indexRequest.index();
        try {
            CreateIndexResponse response = this.client.indices().create( this.indexRequest, RequestOptions.DEFAULT );
            boolean acknowledged = response.isAcknowledged();
            boolean shardsAcknowledged = response.isShardsAcknowledged();
            if (acknowledged && shardsAcknowledged) {
                log.info( "文档[{}]初始化成功: [{}]", index, documentClass.getName() );
            }
        } catch (IOException e) {
            log.error( "文档[{}]初始化失败: [{}]", index, documentClass.getName(), e );
        }
    }
}
