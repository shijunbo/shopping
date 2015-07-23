package com.guogod.shopping.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class GuiceInjectorService {
    private static final Injector INJECTOR;
    private static final List<Module> modules;

    static {
        modules = new ArrayList<Module>();
        modules.add(new GuiceModule());
        INJECTOR = Guice.createInjector(modules);
    }

    public static <T> T getInstance(Class<T> tClass){
        return INJECTOR.getInstance(tClass);
    }

    public static <T> T getInstance(Class<T> tClass,Class<? extends Annotation> option){
        final Key<T> key = Key.get(tClass,option);
        return INJECTOR.getInstance(key);
    }

    public static List<Module> getModules(){
        return modules;
    }
}
