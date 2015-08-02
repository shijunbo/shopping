package com.guogod.shopping.task;

/**
 * Created by shijunbo on 2015/7/16.
 */
public class TaskError extends Error
{
    private static final long serialVersionUID = 1L;
    public TaskError()
    {
    }

    public TaskError(String message) {
        super(message);
    }

    public TaskError(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskError(Throwable cause) {
        super(cause);
    }

    public static class Done extends TaskError
    {
        private static final long serialVersionUID = -3518184323450273047L;
    }

    public static class Repeat extends TaskError{
        public Repeat(String message) {
            super(message);
        }

        public Repeat(String message, Throwable cause) {
            super(message, cause);
        }

        public Repeat(Throwable cause) {
            super(cause);
        }

        private static final long serialVersionUID = -3518184323450273047L;
    }

    public static class ForceCancel extends TaskError
    {
        private static final long serialVersionUID = -3518184323450273047L;
    }
}
