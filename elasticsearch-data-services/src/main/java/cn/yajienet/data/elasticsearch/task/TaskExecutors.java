package cn.yajienet.data.elasticsearch.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description 任务线程池
 */
@Slf4j
public class TaskExecutors {

    private final ThreadPoolExecutor loadDataTaskPool = new ThreadPoolExecutor(
            0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(), new TaskPoolThreadFactory( Executors.defaultThreadFactory(), "Load Data" ), new ThreadPoolExecutor.AbortPolicy() );

    private static class TaskExecutorsHolder {
        private static final TaskExecutors INSTANCE = new TaskExecutors();
    }

    public static TaskExecutors getInstance() {
        return TaskExecutorsHolder.INSTANCE;
    }

    public static void execute(Runnable runnable) {
        getInstance().getLoadDataTaskPool().execute( runnable );
    }

    public static boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return getInstance().getLoadDataTaskPool().awaitTermination( timeout, unit );
    }

    public static boolean await() {
        try {
            return await(Long.MAX_VALUE,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn( "",e );
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public ThreadPoolExecutor getLoadDataTaskPool() {
        return this.loadDataTaskPool;
    }

    public static class TaskPoolThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNum = new AtomicInteger();
        private final ThreadFactory delegate;
        private final String name;

        public TaskPoolThreadFactory(ThreadFactory delegate, String name) {
            this.delegate = delegate;
            this.name = name;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = delegate.newThread( r );
            t.setName( name + " [#" + threadNum.incrementAndGet() + "]" );
            return t;
        }
    }
}
