package cn.yajienet.data.elasticsearch.task;

import cn.yajienet.data.elasticsearch.context.ElasticSearchContext;
import cn.yajienet.data.elasticsearch.data.LoadData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

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
public class TaskManagementCenter extends Thread {

    private final RestTemplate restTemplate;

    public TaskManagementCenter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run() {
        log.info( "-----------  load data elasticsearch start  -----------" );
        try {
            while (true) {
                List<LoadData> loadDataTasks = ElasticSearchContext.getTask();
                if (CollectionUtils.isEmpty( loadDataTasks )) {
                    break;
                }
                // 分配任务
                CountDownLatch countDownLatch = new CountDownLatch( loadDataTasks.size() );
                for (LoadData loadData : loadDataTasks) {
                    TaskExecutors.execute( new TaskLoadData( restTemplate, loadData, countDownLatch ) );

                }
                // 等待此等级所有任务执行完
                countDownLatch.await();
            }
        } catch (InterruptedException e) {
            log.error( "任务管理中心分配任务出错，已经停止运行......" );
        } finally {
            Thread.currentThread().interrupt();
        }
        log.info( "-----------  load data elasticsearch end  -----------" );
    }
}
