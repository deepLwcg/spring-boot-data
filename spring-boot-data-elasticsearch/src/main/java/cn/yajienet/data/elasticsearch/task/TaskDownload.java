package cn.yajienet.data.elasticsearch.task;

import cn.yajienet.data.elasticsearch.data.LoadData;
import cn.yajienet.data.elasticsearch.download.RemoteDownload;
import cn.yajienet.data.elasticsearch.download.DownEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/24
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class TaskDownload implements Runnable {

    private final CountDownLatch childLatch;

    private final RemoteDownload remoteDownload;

    private final List<DownEntity> entities;

    private final LoadData loadData;

    public TaskDownload(CountDownLatch childLatch, RemoteDownload remoteDownload, List<DownEntity> entities, LoadData loadData) {
        this.childLatch = childLatch;
        this.remoteDownload = remoteDownload;
        this.entities = entities;
        this.loadData = loadData;
    }

    @Override
    public void run() {
        try {
            for (DownEntity entity : entities) {
                String json = remoteDownload.download( entity );
                if (StringUtils.hasLength( json )) {
                    loadData.load( json );
                } else {
                    log.error( "[{}] [{}] 子任务未能从远程加载到数据......", loadData.getClass().getSimpleName(), remoteDownload.getClass().getSimpleName() );
                }
            }
        } catch (Exception ex) {
            log.error( "[{}] [{}] 子任务运行出现错误......", loadData.getClass().getSimpleName(), remoteDownload.getClass().getSimpleName(), ex );
        }
        childLatch.countDown();
    }
}
