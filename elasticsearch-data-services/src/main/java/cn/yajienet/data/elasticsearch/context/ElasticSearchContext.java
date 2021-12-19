package cn.yajienet.data.elasticsearch.context;

import cn.yajienet.data.elasticsearch.annotation.Document;
import cn.yajienet.data.elasticsearch.data.LoadData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/16
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class ElasticSearchContext {

    /**
     * 文档对应实体class
     */
    private final Set<Class<?>> documentsClass = new CopyOnWriteArraySet<>();

    /**
     * 所有任务
     */
    private final Map<Integer, List<LoadData>> loadDataTasks = new HashMap<>();

    /**
     * 任务顺序
     */
    private final List<Integer> loadTaskOrder = new ArrayList<>();

    private static class ElasticSearchContextHolder {
        private static final ElasticSearchContext INSTANCE = new ElasticSearchContext();

    }

    private ElasticSearchContext() {

    }

    private static ElasticSearchContext context() {
        return ElasticSearchContextHolder.INSTANCE;
    }

    public static void addDocumentsClassAll(Set<Class<?>> classSet) {
        context().documentsClass.addAll( classSet );
    }

    public static void addDocumentsClass(Class<?> aClass) {
        context().documentsClass.add( aClass );
    }

    public static Set<Class<?>> getDocumentsClass() {
        return context().documentsClass;
    }

    public static void checkDocuments() {
        List<Class<?>> classList = new ArrayList<>( context().documentsClass );
        Map<String, Integer> documentIndex = new HashMap<>( classList.size() );
        for (int i = classList.size() - 1; i >= 0; i--) {
            Class<?> aClass = classList.get( i );
            Document document = aClass.getAnnotation( Document.class );
            if (!StringUtils.hasLength( document.index() )) {
                log.warn( "[{}]未设置索引(Index)，跳过初始化此文档！", aClass.getName() );
                classList.remove( i );
            } else {
                if (documentIndex.containsKey( document.index() )) {
                    Integer pre = documentIndex.get( document.index() );
                    Class<?> preClass = classList.get( pre );
                    log.warn( "文档[{}]索引(Index)重复，跳过初始化此文档......", aClass.getName() );
                    log.warn( "同时移出重复文档[{}]的初始化......", preClass.getName() );
                    classList.remove( pre.intValue() );
                    classList.remove( i );
                } else {
                    documentIndex.put( document.index(), i );
                }
            }
        }
        context().documentsClass.clear();
        context().documentsClass.addAll( classList );
    }

    public static void addTasK(int order, LoadData loadData) {
        synchronized (context().loadTaskOrder) {
            if (context().loadTaskOrder.contains( order )) {
                List<LoadData> loadDataList = context().loadDataTasks.get( order );
                loadDataList.add( loadData );
            } else {
                context().loadTaskOrder.add( order );
                List<LoadData> loadDataList = new ArrayList<>();
                loadDataList.add( loadData );
                context().loadDataTasks.put( order, loadDataList );
                context().loadTaskOrder.sort( (o1, o2) -> o2 - o1 );
            }
        }
    }

    public static List<LoadData> getTask() {
        synchronized (context().loadTaskOrder) {
            if (CollectionUtils.isEmpty( context().loadTaskOrder )) {
                // 所有任务已经处理完成
                return Collections.emptyList();
            }
            int order = context().loadTaskOrder.get( context().loadTaskOrder.size() - 1 );
            context().loadTaskOrder.remove( context().loadTaskOrder.size() - 1 );
            List<LoadData> loadDataList = context().loadDataTasks.get( order );
            context().loadDataTasks.remove( order );
            return loadDataList;
        }
    }
}

