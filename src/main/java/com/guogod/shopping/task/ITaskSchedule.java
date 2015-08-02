package com.guogod.shopping.task;

/**
 * Created by shijunbo on 2015/7/16.
 */
public interface ITaskSchedule<T extends ISubTask> {
    void setTickPollInterval(int interval);

    int getTickPollInterval();

    void stop();

    void start();

    //任务已经被调度,但并不代表进入running状态，只有ISubTask调用了onStart，才代表Running状态
    boolean isScheduling();

    boolean isRunning();

    //任务正常结束，非cancel or exception
    boolean isDone();

    boolean isCanceled();

    T getScheduleTask();

    void setRepeat(int repeat);

    int getRepeat();
}
