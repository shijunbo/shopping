package com.guogod.shopping.task;

/**
 * Created by shijunbo on 2015/7/16.
 */
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.guogod.shopping.executor.JExecutor;

public class TickTaskSchedule<T extends ISubTask> implements Runnable, ITaskSchedule<T> {
    public static final int DEFAULT_TICK_POLL_INTERVAL = 5000;
    public static final int DEFAULT_REPEAT = 10;

    long _last_tick_time = 0;
    T _task;
    volatile boolean _isCancel = false;
    volatile boolean _isDone= false;
    volatile boolean _isRunning = false;
    volatile boolean _isScheduling = false;
    volatile boolean _isRealCancel = false;
    volatile int _interval = DEFAULT_TICK_POLL_INTERVAL;
    volatile int _repeats = DEFAULT_REPEAT;
    volatile int _renums = 0;

    static Logger LOGGER = LoggerFactory
            .getLogger(TickTaskSchedule.class);

    public TickTaskSchedule()
    {
    }

    public TickTaskSchedule(T task)
    {
        _task = task;
    }

    public TickTaskSchedule(T task, int poll_interval)
    {
        _task = task;
        setTickPollInterval(poll_interval);
    }

    public TickTaskSchedule(T task, int poll_interval, int repeats)
    {
        _task = task;
        setTickPollInterval(poll_interval);
        setRepeat(repeats);
    }

    public void setScheduleTask(T task, int poll_interval, int repeats)
    {
        _task = task;
        setTickPollInterval(poll_interval);
        setRepeat(repeats);
    }

    void reset(){
        _renums = 0;
    }

    void scheduleNextTick(TickRunnable t, long nexttime)
    {
        if (nexttime <= 0)
        {
            t.run();//use same thread
        }else
        {
            JExecutor.getScheduleExecutor().schedule(t, nexttime, TimeUnit.MILLISECONDS);
        }
    }

    class TickRunnable implements Runnable
    {
        private Runnable r;
        TickRunnable(Runnable r)
        {
            this.r = r;
        }

        @Override
        public void run()
        {

            try
            {
                if (_isCancel)
                {
                    _isRealCancel = true;
                    _task.onCancel();
                }else
                {
                    try
                    {
                        r.run();
                        return;
                    }catch(TaskError.Done e)
                    {
                        _task.onEnd();
                        _isDone = true;
                    }
                }
            }
            catch(Throwable e)
            {
                try
                {
                    if (e instanceof TaskError)
                    {
                        _isRealCancel = true;
                        _task.onException((TaskError)e);
                    }
                    else
                    {
                        _isRealCancel = true;
                        _task.onException(new TaskError(e));
                    }
                }catch(Throwable ee){}; //maybe onException throw a exception
            }

            _isRunning = false;
            _isScheduling = false;

        }

    }

    class StartTick implements Runnable
    {
        @Override
        public void run()
        {
            _isRunning = true;
            try{
                _task.onStart();
                reset();
            }catch(TaskError.Repeat r){
                LOGGER.debug("_task {} renum {} repeat {} r {}", _task, _renums, _repeats, r);
                if( _renums++ >= _repeats ){
                    throw r;
                }
                scheduleNextTick(new TickRunnable(new StartTick()), getTickPollInterval());
            }
            _last_tick_time = System.currentTimeMillis();
        }
    }

    class TaskTick implements Runnable
    {
        @Override
        public void run()
        {
            long diff = System.currentTimeMillis() - _last_tick_time;
            _last_tick_time = System.currentTimeMillis();
            try{
                _task.onTick(diff);
                reset();
            }catch(TaskError.Repeat r){
                LOGGER.debug("_task {} renum {} repeat {} r {}", _task, _renums, _repeats, r);
                if( _renums++ >= _repeats ){
                    throw r;
                }
            }

            scheduleNextTick(new TickRunnable(new TaskTick()), getTickPollInterval());
        }
    }

    @Override
    public void run()
    {
        new TickRunnable(new StartTick()).run();
        reset();
        new TickRunnable(new TaskTick()).run();
    }


    @Override
    public void setTickPollInterval(int interval)
    {
        _interval = interval;
    }


    @Override
    public int getTickPollInterval()
    {
        return _interval;
    }

    @Override
    public void stop()
    {
        if (_isDone)
        {
            return;
        }

        if (_isScheduling){
            _isCancel = true;
        }
        else{
            _isRealCancel = true;
//			_task.onException(new TaskError());
        }
    }

    @Override
    public void start()
    {
        if (_isScheduling || _isCancel)
        {
            return;
        }
        _isScheduling = true;

        JExecutor.getWorkerExecutor().submit(this);
    }

    @Override
    public boolean isScheduling()
    {
        return _isScheduling;
    }

    @Override
    public T getScheduleTask()
    {
        return _task;
    }

    @Override
    public boolean isRunning()
    {
        return _isRunning;
    }

    @Override
    public boolean isDone()
    {
        return _isDone;
    }

    @Override
    public boolean isCanceled()
    {
        return _isRealCancel;
    }

    @Override
    public void setRepeat(int repeat) {
        _repeats = repeat;
    }

    @Override
    public int getRepeat() {
        return _repeats;
    }
}
