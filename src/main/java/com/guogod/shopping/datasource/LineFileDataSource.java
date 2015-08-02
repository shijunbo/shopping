package com.guogod.shopping.datasource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by shijunbo on 2015/7/17.
 */
public class LineFileDataSource implements DataSource<String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineFileDataSource.class);
    private BufferedReader bufferedReader = null;
    private String nextLine = null;
    private File file;

    public LineFileDataSource(File file){
        this.file = file;
    }

    @Override
    public void open() throws IOException {
        //add by mahongming
        bufferedReader = new BufferedReader(new FileReader(file));
        nextLine = bufferedReader.readLine();
    }

    @Override
    public boolean hasNext() {
        return null != nextLine;
    }

    @Override
    public String next() throws IOException {
        String tmp = nextLine;
        nextLine = bufferedReader.readLine();
        return tmp;
    }

    @Override
    public void close() {
        IOUtils.closeQuietly(bufferedReader);
    }

    @Override
    public void setDataSource(Object obj) {
        this.file =(File)obj;
    }
}
