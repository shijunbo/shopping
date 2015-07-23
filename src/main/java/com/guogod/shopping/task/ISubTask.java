package com.guogod.shopping.task;

/**
 * Created by shijunbo on 2015/7/16.
 */
public interface ISubTask {
    void onStart();

    void onEnd();

    void onTick(long elapse);

    //if the task be canced before onStart(), onStart will not execute, onEnd so on
    void onCancel();

    void onException(TaskError e);

    float getProgress();
}
