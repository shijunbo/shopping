package com.guogod.shopping.executor;

/**
 * Created by shijunbo on 2015/7/16.
 */
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JExecutor {
    public static final int WORKER_THREAD_NUM = 3;
    public static final int SCHEDULE_THREAD_NUM = WORKER_THREAD_NUM / 3 + 1;
    static private ScheduledExecutorService _scheduleExecutor =
            new ScheduleExecutorWrapper(SCHEDULE_THREAD_NUM, new _WorkThreadFactory("Schedule"));

    static private ExecutorService _workerExecutor =
            new WorkPoolExecutor(WORKER_THREAD_NUM);


    static Logger LOGGER = LoggerFactory.getLogger(JExecutor.class);

    static class WorkPoolExecutor extends ThreadPoolExecutor
    {
        WorkPoolExecutor(int thread_num)
        {
            super(thread_num, thread_num,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    new _WorkThreadFactory("Worker"));
        }

        @Override
        protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value)
        {
            return super.newTaskFor(
                    decorateRunnable(runnable), value);//submit .get
        }

        @Override
        public void execute(Runnable command)
        {
            if (command != null)
            {
                command = decorateRunnable(command);
            }
            super.execute(command);
        }

    }

    static Runnable decorateRunnable(Runnable r)
    {
        return decorateLoggerRunnable(r);
    }

    static Runnable decorateLoggerRunnable(Runnable r)
    {
        return new RunnableLoggerWrapper(r);
    }

    static class RunnableLoggerWrapper implements Runnable
    {
        private Runnable r;
        public RunnableLoggerWrapper(Runnable r) {
            this.r = r;
        }
        @Override
        public void run() {
            try
            {
                r.run();
            }catch(Throwable e)
            {
                LOGGER.warn("", e);
                if (e instanceof Error)
                {
                    throw (Error)e;
                }
                if(e instanceof RuntimeException)
                {
                    throw (RuntimeException)e;
                }
                throw new Error(e);
            }
        }
    }

    static private class _WorkThreadFactory implements ThreadFactory
    {
        AtomicLong _workThreadID = new AtomicLong(0);

        private String _prefix;
        public _WorkThreadFactory(String prefix)
        {
            _prefix = prefix;
        }

        class WorkThread extends Thread
        {
            public WorkThread(Runnable r)
            {
                super(r, _prefix + "-" + String.valueOf(_workThreadID.incrementAndGet()));
            }
        }

        @Override
        public Thread newThread(Runnable r)
        {
            return new WorkThread(r);
        }
    }


    static private class ScheduleExecutorWrapper extends  ScheduledThreadPoolExecutor
    {

        public ScheduleExecutorWrapper(int corePoolSize,
                                       ThreadFactory threadFactory) {
            super(corePoolSize, threadFactory);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return super.submit(decorateRunnable(task), result);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return super.submit(decorateRunnable(task));
        }

        @Override
        public void execute(Runnable command) {
            super.execute(decorateRunnable(command));
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay,
                                           TimeUnit unit) {
            return super.schedule(decorateRunnable(command), delay, unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                               long delay, TimeUnit unit) {
            return super.schedule(callable, delay, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                      long initialDelay, long period, TimeUnit unit) {
            return super.scheduleAtFixedRate(decorateRunnable(command), initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                         long initialDelay, long delay, TimeUnit unit) {
            return super.scheduleWithFixedDelay(decorateRunnable(command), initialDelay, delay, unit);
        }
    }

    static public ScheduledExecutorService getScheduleExecutor()
    {
        return _scheduleExecutor;
    }

    static public ExecutorService getWorkerExecutor()
    {
        return _workerExecutor;
    }
}
