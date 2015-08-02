package com.guogod.shopping.datasource;

import java.io.IOException;

/**
 * Created by shijunbo on 2015/7/17.
 */
public interface DataSource<T> {
    public void open() throws IOException;
    public boolean hasNext();
    public T next() throws IOException;
    public void close();
    public void setDataSource(Object obj);
}
