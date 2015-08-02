package com.guogod.shopping.guice;

/**
 * Created by shijunbo on 2015/7/15.
 */
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.Mongo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.configuration.Configuration;

public class DatastoreProvider implements Provider<Datastore> {
    private Configuration configuration;
    private Mongo mongo;
    private Morphia morphia;

    @Inject
    public DatastoreProvider(Configuration configuration, Mongo mongo, Morphia morphia) {
        this.configuration = configuration;
        this.mongo = mongo;
        this.morphia = morphia;
    }

    @Override
    public Datastore get() {
        if (configuration.containsKey("mongodb.username") && null != StringUtils.stripToNull(configuration.getString("mongodb.username"))) {
            return morphia.createDatastore(mongo,
                    configuration.getString("mongodb.db")
                    , configuration.getString("mongodb.username"),
                    configuration.getString("mongodb.password")
                            .toCharArray());
        }
        return morphia.createDatastore(mongo,
                configuration.getString("mongodb.db"));
    }
}
