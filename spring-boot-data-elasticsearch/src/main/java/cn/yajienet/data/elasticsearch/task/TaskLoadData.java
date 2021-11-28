package cn.yajienet.data.elasticsearch.task;

import cn.yajienet.data.elasticsearch.annotation.Download;
import cn.yajienet.data.elasticsearch.data.LoadData;
import cn.yajienet.data.elasticsearch.download.RemoteDownload;
import cn.yajienet.data.elasticsearch.download.DownEntity;
import cn.yajienet.data.elasticsearch.utils.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description
 */
@Slf4j
public class TaskLoadData implements Runnable {

    private static final Pattern URL_PATTERN = Pattern.compile( "^https?://[^\\s]*" );

    private final LoadData loadData;

    private final CountDownLatch countDownLatch;

    public TaskLoadData(LoadData loadData, CountDownLatch countDownLatch) {
        this.loadData = loadData;
        this.countDownLatch = countDownLatch;
    }


    @Override
    public void run() {
        // 加载下载器
        Download download = loadData.getClass().getAnnotation( Download.class );
        String uri = loadData.remoteUri();
        if (Objects.isNull( download ) || StringUtils.isEmpty( uri ) || !(URL_PATTERN.matcher( uri ).matches())) {
            // 没有下载器，直接运行
            log.warn( "[{}]没有配置下载器或者没有配置入口URI，无法从远程获取数据......", loadData.getClass().getSimpleName() );
            loadData.load( null );
        } else {
            // 加载下载器，开始远程获取数据
            try {
                RemoteDownload remoteDownload = download.remote().newInstance();
                remoteDownload.setRestTemplate( RestTemplateUtils.restTemplate() );
                List<DownEntity> entities = remoteDownload.parseUrls( uri );
                if (!CollectionUtils.isEmpty( entities )) {
                    // 创建子任务，为每个子线程分发任务，扑克发牌方式
                    Map<Integer, List<DownEntity>> downloadTask = new HashMap<>(download.maxThreads());
                    for (int i = 0; i < entities.size(); i++) {
                        // 分配任务
                        int taskIndex = i % download.maxThreads();
                        List<DownEntity> uriTasks = null;
                        if (downloadTask.containsKey( taskIndex )) {
                            uriTasks = downloadTask.get( taskIndex );
                        } else {
                            uriTasks = new ArrayList<>();
                            downloadTask.put( taskIndex, uriTasks );
                        }
                        uriTasks.add( entities.get( i ) );
                    }
                    // 开始创建子任务
                    CountDownLatch childCountDownLatch = new CountDownLatch( downloadTask.size() );
                    for (Map.Entry<Integer, List<DownEntity>> entry : downloadTask.entrySet()) {
                        TaskDownload taskDownload = new TaskDownload( childCountDownLatch, remoteDownload, entry.getValue(), loadData );
                        TaskExecutors.execute( taskDownload );
                    }
                    childCountDownLatch.await();
                } else {
                    log.warn( "[{}]下载器解析所有URL失败......", remoteDownload.getClass().getSimpleName() );
                }
            } catch (InstantiationException | IllegalAccessException | InterruptedException e) {
                log.error( "[{}]获取下载器失败......", loadData.getClass().getSimpleName() );
            }
        }
        countDownLatch.countDown();
    }
}
