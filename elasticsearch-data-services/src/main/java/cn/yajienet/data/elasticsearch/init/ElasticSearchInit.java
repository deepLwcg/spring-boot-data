package cn.yajienet.data.elasticsearch.init;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.annotation.LoadOrder;
import cn.yajienet.data.elasticsearch.context.ElasticSearchContext;
import cn.yajienet.data.elasticsearch.data.LoadData;
import cn.yajienet.data.elasticsearch.document.InitializeDocument;
import cn.yajienet.data.elasticsearch.mapping.Mapping;
import cn.yajienet.data.elasticsearch.properties.ElasticSearchProperties;
import cn.yajienet.data.elasticsearch.setting.Setting;
import cn.yajienet.data.elasticsearch.task.TaskManagementCenter;
import cn.yajienet.data.elasticsearch.utils.ClassScan;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static cn.yajienet.data.elasticsearch.ElasticsearchConstant.DEFAULT_LOAD_DATA_ORDER;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/12/19
 * @Version 1.0.0
 * @Description
 */
@Slf4j
@Component
public class ElasticSearchInit {

    /**
     * 所有文档对应的数据加载对象
     */
    private final Map<Class<?>, LoadData> loadDataBeans = new HashMap<>();

    /**
     * 初始化错误的文档
     */
    private final List<Class<?>> errorDocument = new ArrayList<>();


    private final ElasticSearchProperties elasticSearchProperties;
    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;

    public ElasticSearchInit(ElasticSearchProperties elasticSearchProperties, ApplicationContext applicationContext, RestTemplate restTemplate) {
        this.elasticSearchProperties = elasticSearchProperties;
        this.applicationContext = applicationContext;
        this.restTemplate = restTemplate;
    }


    public void init() {
        log.info( "---------- start initialize elasticsearch ----------" );
        scanDocumentScanPackages();
        // 先检查文档索引
        ElasticSearchContext.checkDocuments();
        // 开始初始化
        scanLoadData();
        initializeDocument();
        log.info( "----------  initialize elasticsearch end  ----------" );
        initializeData();
        startLoadData();
        System.exit( 200 );
    }

    /**
     * 扫面配置的路径，并打印已经扫描到的文档
     *
     * @author Wang Chenguang
     * @date 2021/12/19
     */
    private void scanDocumentScanPackages() {
        List<String> documentScans = elasticSearchProperties.getDocumentScanPackages();
        if (!CollectionUtils.isEmpty( documentScans )) {
            List<String> basePackages = documentScans.stream().filter( StringUtils::hasLength ).collect( Collectors.toList() );
            if (!basePackages.isEmpty()) {
                String[] propertiesBasePackages = new String[basePackages.size()];
                basePackages.toArray( propertiesBasePackages );
                Set<Class<?>> documentScanClass = ClassScan.scan( propertiesBasePackages, Document.class );
                if (!CollectionUtils.isEmpty( documentScanClass )) {
                    ElasticSearchContext.addDocumentsClassAll( documentScanClass );
                }
            }
        }
        Set<Class<?>> classSet = ElasticSearchContext.getDocumentsClass();
        if (CollectionUtils.isEmpty( classSet )) {
            log.warn( "EnableElasticSearchServices annotation not scan." );
        } else {
            classSet.forEach( aClass -> log.info( "EnableElasticSearchServices annotation scans documents: {}", aClass.getName() ) );
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
                log.error( "文档[{}]已经加载到对应初始化数据对象，跳过当前[{}]load data.....", aClass.getSimpleName(), loadData.getClass().getSimpleName() );
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
                initializeDocument.setClient( applicationContext.getBean( RestHighLevelClient.class ) );
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
                loadData.setClient( applicationContext.getBean( RestHighLevelClient.class ) );
                loadData.setRestTemplate( restTemplate );
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
        TaskManagementCenter taskManagementCenter = new TaskManagementCenter( restTemplate );
        taskManagementCenter.start();
        try {
            taskManagementCenter.join();
        } catch (InterruptedException e) {
            log.error( e.getMessage(), e );
            Thread.currentThread().interrupt();
        }
    }

}
