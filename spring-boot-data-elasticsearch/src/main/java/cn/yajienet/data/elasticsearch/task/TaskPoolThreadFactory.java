package cn.yajienet.data.elasticsearch.task;

import org.springframework.lang.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author Wang Chenguang
 * @Email wcg.chen@foxmail.com
 * @Date on 2021/10/23
 * @Version 1.0.0
 * @Description
 */
public class TaskPoolThreadFactory implements ThreadFactory {

    private final AtomicInteger threadNum = new AtomicInteger();
    private final ThreadFactory delegate;
    private final String name;

    public TaskPoolThreadFactory(ThreadFactory delegate, String name) {
        this.delegate = delegate;
        this.name = name;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread t = delegate.newThread(r);
        t.setName(name + " [#" + threadNum.incrementAndGet() + "]");
        return t;
    }
}
