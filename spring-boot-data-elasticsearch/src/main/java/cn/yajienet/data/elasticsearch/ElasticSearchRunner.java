package cn.yajienet.data.elasticsearch;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.LoadOrder;
import cn.yajienet.data.elasticsearch.config.ElasticSearchDataProperties;
import cn.yajienet.data.elasticsearch.context.ElasticSearchContext;
import cn.yajienet.data.elasticsearch.data.LoadData;
import cn.yajienet.data.elasticsearch.document.InitializeDocument;
import cn.yajienet.data.elasticsearch.es.Mapping;
import cn.yajienet.data.elasticsearch.es.Setting;
import cn.yajienet.data.elasticsearch.task.TaskExecutors;
import cn.yajienet.data.elasticsearch.task.TaskManagementCenter;
import cn.yajienet.data.elasticsearch.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

import static cn.yajienet.data.elasticsearch.ElasticSearchDataConstant.DEFAULT_LOAD_DATA_ORDER;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description 初始化elasticsearch
 */
@Slf4j
@Component
@Order(1)
public class ElasticSearchRunner implements ApplicationRunner {

    /**
     * 所有文档对应的数据加载对象
     */
    private final Map<Class<?>, LoadData> loadDataBeans = new HashMap<>();

    /**
     * 初始化错误的文档
     */
    private final List<Class<?>> errorDocument = new ArrayList<>();



    private final ElasticSearchDataProperties elasticSearchDataProperties;

    private final RestHighLevelClient restHighLevelClient;

    private final ApplicationContext applicationContext;


    public ElasticSearchRunner(ElasticSearchDataProperties elasticSearchDataProperties, ApplicationContext applicationContext) {
        this.elasticSearchDataProperties = elasticSearchDataProperties;
        this.applicationContext = applicationContext;
        restHighLevelClient = applicationContext.getBean( RestHighLevelClient.class );
    }

    @Override
    public void run(ApplicationArguments args) {
        switch (elasticSearchDataProperties.getType()){
            case ElasticSearchDataConstant.RUN_MODE_NONE:
                log.info( "elasticSearch data 不做任何工作，type is none" );
                break;
            case ElasticSearchDataConstant.RUN_MODE_INIT:
                initRunModel();
                break;
            case ElasticSearchDataConstant.RUN_MODE_UPDATE:
                updateRunMode();
                break;
            case ElasticSearchDataConstant.RUN_MODE_DELETE:
                deleteRunMode();
                break;
            default:
                log.info( "elasticSearch data type is error" );
        }
        if (elasticSearchDataProperties.getScript()) {
            System.exit( 200 );
        }
    }

    /**
     * init run model
     *
     * @author Wang Chenguang
     * @date 2021/11/13
     */
    private void initRunModel(){
        log.info( "---------- start initialize elasticsearch ----------" );
        // 先检查文档索引
        ElasticSearchContext.checkDocuments();
        // 开始初始化
        scanLoadData();
        initializeDocument();
        initializeData();
        startLoadData();
        log.info( "----------  initialize elasticsearch end  ----------" );
    }

    /**
     * update run model
     *
     * @author Wang Chenguang
     * @date 2021/11/13
     */
    private void updateRunMode(){
        log.info( "---------- start update elasticsearch ----------" );
        // 先检查文档索引
        ElasticSearchContext.checkDocuments();
        // 开始初始化
        scanLoadData();
        initializeData();
        startLoadData();
        log.info( "----------  update elasticsearch end  ----------" );
    }

    /**
     * delete run mode
     *
     * @author Wang Chenguang
     * @date 2021/11/13
     */
    private void deleteRunMode() {
        log.info( "---------- start delete elasticsearch ----------" );
        // 先检查文档索引
        ElasticSearchContext.checkDocuments();
        deleteDocuments();
        log.info( "----------  delete elasticsearch end  ----------" );
    }

    /**
     * 删除文档
     *
     * @author Wang Chenguang
     * @date 2021/11/14
     */
    private void deleteDocuments() {
        List<Class<?>> classList = new ArrayList<>( ElasticSearchContext.getDocumentsClass() );
        for (Class<?> aClass : classList) {

        }
    }

    /**
     * 查找文档对应的初始化数据组件
     *
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    private void scanLoadData() {
        List<Class<?>> classList = new ArrayList<>( ElasticSearchContext.getDocumentsClass() );
        ObjectProvider<LoadData> loadDataObjectProvider = applicationContext.getBeanProvider( LoadData.class );
        for (LoadData loadData : loadDataObjectProvider) {
            Class<?> aClass = loadData.getDocumentClass();
            if (loadDataBeans.containsKey( aClass )) {
                log.error( "此文档[{}]已经加载到对应初始化数据对象，跳过当前[{}]load data.....", aClass.getSimpleName(), loadData.getClass().getSimpleName() );
                continue;
            }
            if (classList.contains( aClass )) {
                loadDataBeans.put( aClass, loadData );
                classList.remove( aClass );
            } else {
                log.warn( "初始化数据[{}]定义文档[{}]未扫描到，文档可能配置有误！", loadData.getClass().getName(), aClass.getName() );
            }
        }
        for (Class<?> aClass : classList) {
            log.warn( "文档[{}]未定义初始化数据功能，若需要请继承AbstractLoadDataAdapter类！", aClass.getName() );
        }
    }


    /**
     * 初始化文档
     *
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    private void initializeDocument() {
        List<Class<?>> classList = new ArrayList<>( ElasticSearchContext.getDocumentsClass() );
        for (Class<?> aClass : classList) {
            try {
                Document document = aClass.getAnnotation( Document.class );

                InitializeDocument initializeDocument = document.initializeDocument().newInstance();

                Setting defaultSetting = document.setting().newInstance();
                defaultSetting.setDocumentClass( aClass );

                Mapping autoMapping = document.mapping().newInstance();
                autoMapping.setDocumentClass( aClass );

                initializeDocument.setClient( restHighLevelClient );
                initializeDocument.setDocumentClass( aClass );

                initializeDocument.setSetting( defaultSetting );
                initializeDocument.setMapping( autoMapping );

                if (initializeDocument.delete()) {
                    initializeDocument.initialize();
                    log.info( "已经完成文档[{} - {}]的结构的初始化！", aClass.getName(), document.index() );
                } else {
                    log.info( "原文档[{} - {}]未删除不能初始化！", aClass.getName(), document.index() );
                    errorDocument.add( aClass );
                }
            } catch (Exception e) {
                log.error( "未获取到文档[{}]对象，无法初始化此文档！", aClass.getName(), e );
                errorDocument.add( aClass );
            }
        }
    }

    /**
     * 初始化数据
     *
     * @author Wang Chenguang
     * @date 2021/10/17
     */
    private void initializeData() {
        for (Map.Entry<Class<?>, LoadData> entry : loadDataBeans.entrySet()) {
            Class<?> documentClass = entry.getKey();
            LoadData loadData = entry.getValue();
            if (errorDocument.contains( documentClass )) {
                log.error( "没有初始化文档[{}]，跳过加载数据！", documentClass.getName() );
            } else {
                loadData.setClient( restHighLevelClient );
                loadData.setRestTemplate( RestTemplateUtils.restTemplate() );
                LoadOrder loadOrder = loadData.getClass().getAnnotation( LoadOrder.class );
                if (Objects.isNull( loadOrder )) {
                    ElasticSearchContext.addTasK( DEFAULT_LOAD_DATA_ORDER, loadData );
                } else {
                    ElasticSearchContext.addTasK( loadOrder.value(), loadData );
                }
                log.info( "文档[{}]已经成功加载到任务列......", documentClass.getName() );
            }
        }
    }

    private void startLoadData() {
        // 文档初始化完成，启动任务管理中心
        TaskExecutors.execute( new TaskManagementCenter() );
    }
}
