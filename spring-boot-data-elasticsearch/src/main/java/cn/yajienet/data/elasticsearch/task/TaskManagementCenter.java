package cn.yajienet.data.elasticsearch.task;

import cn.yajienet.data.elasticsearch.context.ElasticSearchContext;
import cn.yajienet.data.elasticsearch.data.LoadData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description 任务管理中心
 */
@Slf4j
public class TaskManagementCenter implements Runnable {


    @Override
    public void run() {
        while (true) {
            List<LoadData> loadDataTasks = ElasticSearchContext.getTask();
            if (CollectionUtils.isEmpty( loadDataTasks )) {
                break;
            }
            // 分配任务
            CountDownLatch countDownLatch = new CountDownLatch( loadDataTasks.size() );
            for (LoadData loadData : loadDataTasks) {
                TaskExecutors.execute( new TaskLoadData( loadData, countDownLatch ) );

            }
            // 等待此等级所有任务执行完
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                log.error( "任务管理中心分配任务出错，已经停止运行......" );
                break;
            }
        }
        log.info( "-----------  load data elasticsearch end  -----------" );
    }
}
