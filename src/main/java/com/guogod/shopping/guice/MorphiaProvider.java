package com.guogod.shopping.guice;

import com.google.code.morphia.Morphia;
import com.google.inject.Provider;
/**
 * Created by shijunbo on 2015/7/15.
 */

public class MorphiaProvider implements Provider<Morphia> {

    @Override
    public Morphia get() {
        return new Morphia();
    }
}
