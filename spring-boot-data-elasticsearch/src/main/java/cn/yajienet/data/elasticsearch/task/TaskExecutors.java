package cn.yajienet.data.elasticsearch.task;

import java.util.concurrent.*;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description
 */
public class TaskExecutors {

    private final ExecutorService LoadDataTaskPool = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new TaskPoolThreadFactory( Executors.defaultThreadFactory(), "Load Data" ), new ThreadPoolExecutor.AbortPolicy() );

    private static class TaskExecutorsHolder {
        private final static TaskExecutors INSTANCE = new TaskExecutors();
    }

    public static TaskExecutors getInstance() {
        return TaskExecutorsHolder.INSTANCE;
    }

    public static void execute(Runnable runnable) {
        getInstance().getLoadDataTaskPool().execute( runnable );
    }

    public ExecutorService getLoadDataTaskPool() {
        return LoadDataTaskPool;
    }
}
