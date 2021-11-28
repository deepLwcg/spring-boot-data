package cn.yajienet.data.elasticsearch.client;

import cn.yajienet.data.elasticsearch.ElasticsearchSearchException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Slf4j
@Component
public class EsClient {


    private final RestHighLevelClient client;

    public EsClient(RestHighLevelClient restHighLevelClient) {
        this.client = restHighLevelClient;
    }

    public RestHighLevelClient getClient() {
        return client;
    }

    public ExecuteQuery searchRequest(Consumer<? super SearchRequest> consumer) {
        SearchRequest searchRequest = new SearchRequest();
        if (Objects.nonNull(consumer)) {
            consumer.accept(searchRequest);
        }
        return new ExecuteQuery(this.client, searchRequest);
    }

    public ExecuteQuery searchRequest(SearchRequest request) {
        return new ExecuteQuery( this.client, request );
    }


    public static class ExecuteQuery {
        private final SearchRequest request;
        private final RestHighLevelClient client;
        private SearchResponse response;

        public ExecuteQuery(RestHighLevelClient client, SearchRequest request) {
            this.request = request;
            this.client = client;
        }

        public ExecuteQuery searchSourceBuilder(Consumer<? super SearchSourceBuilder> consumer) {
            if (Objects.nonNull( consumer )) {
                SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource();
                consumer.accept( sourceBuilder );
                this.request.source( sourceBuilder );
            }
            return this;
        }

        public ExecuteQuery search() {
            try {
                this.response = this.client.search(this.request, RequestOptions.DEFAULT);
                log.info("elasticsearch search \n[\n search request: {} \n]", this.request);
            } catch (IOException e) {
                throw new ElasticsearchSearchException("elasticsearch search filed", e);
            }
            return this;
        }

        public void searchAsync(BiConsumer<? super SearchResponse, ? super ExecuteQuery> biConsumer) {
            ActionListener<SearchResponse> actionListener = new ActionListener<SearchResponse>() {
                @Override
                public void onResponse(SearchResponse searchResponse) {
                    if (Objects.nonNull(biConsumer)) {
                        response = searchResponse;
                        biConsumer.accept(searchResponse, ExecuteQuery.this);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    log.error("elasticsearch async search filed, \n search request: {}\n message:{}", request, e);
                }
            };
            client.searchAsync(request, RequestOptions.DEFAULT, actionListener);
        }

        public SearchRequest request() {
            return request;
        }

        public RestHighLevelClient client() {
            return client;
        }

        public SearchResponse response() {
            return response;
        }
    }

}
