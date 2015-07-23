package com.guogod.shopping.guice;

import java.util.ArrayList;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class MongoProvider implements Provider<Mongo> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoProvider.class);
    private Configuration configuration;
    private boolean isReadSecondary = false;

    @Inject
    public MongoProvider(Configuration configuration) {
        this.configuration = configuration;
        isReadSecondary = this.configuration.getBoolean("mongodb.isReadSecondary", false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Mongo get() {
        try {
            String mongodbAddress = configuration.getString("mongodb.address");
            String[] addresses = mongodbAddress.split(",");
            ArrayList<ServerAddress> servers = new ArrayList<ServerAddress>();
            for (String address : addresses) {
                String[] addPort = address.split(":");
                String add = addPort[0];
                int port = addPort.length > 1 ? Integer.parseInt(addPort[1]) : 27017;
                servers.add(new ServerAddress(add, port));
            }
            LOGGER.info("mongo servers:" + mongodbAddress);
//            MongoOptions mongoOptions = new MongoOptions();
//            mongoOptions.connectionsPerHost = this.configuration.getInt("mongodb.connectionsPerHost", 60);
//            mongoOptions.socketTimeout = 60000;
//            mongoOptions.connectTimeout = 30000;
//            mongoOptions.autoConnectRetry = true;
//            mongoOptions.socketKeepAlive = true;
//            Mongo mongo = new Mongo(servers, mongoOptions);
            MongoClientOptions.Builder builder = MongoClientOptions.builder();
            builder.socketKeepAlive(true)
                    .socketTimeout(60000)
                    .connectTimeout(30000)
                    .autoConnectRetry(true)
                    .connectionsPerHost(this.configuration.getInt("mongodb.connectionsPerHost", 60));
            MongoClient mongoClient = new MongoClient(servers, builder.build());
            if (isReadSecondary) mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
            return mongoClient;
        } catch (Throwable e) {
            LOGGER.error("failed to init mongodb", e);
            throw new ExceptionInInitializerError(e);
        }
    }
}
