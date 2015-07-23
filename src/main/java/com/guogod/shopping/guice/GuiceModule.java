package com.guogod.shopping.guice;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.guogod.shopping.rest.api.RecommandMobileServerResource;
import com.guogod.shopping.service.AccessToken;
import com.guogod.shopping.service.ApiService;
import com.guogod.shopping.service.ApiServiceImpl;
import com.guogod.shopping.utils.CommonUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guogod.shopping.http.CloseableHttpClientProvider;
//import com.dianxinos.jedis.wrapper.serializer.JacksonJsonRedisSerializer;
//import com.dianxinos.jedis.wrapper.serializer.RedisSerializer;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.mongodb.Mongo;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class GuiceModule extends AbstractModule{
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceModule.class);

    @Override
    protected void configure() {
        // provider
        bind(SecureRandom.class).toProvider(SecureRandomProvider.class).in(Singleton.class);
        bind(PublicKey.class).toProvider(RSAPublicKeyProvider.class).in(Singleton.class);
        bind(PrivateKey.class).toProvider(RSAPrivateKeyProvider.class).in(Singleton.class);
        bind(HttpClient.class).toProvider(CloseableHttpClientProvider.class);

        bind(RecommandMobileServerResource.class);
        bind(AccessToken.class).in(Singleton.class);
        bind(ApiService.class).to(ApiServiceImpl.class).in(Singleton.class);
        // DI mongodb
        bind(Mongo.class).toProvider(MongoProvider.class).in(Singleton.class);
        bind(Morphia.class).toProvider(MorphiaProvider.class).in(Singleton.class);
        bind(Datastore.class).toProvider(DatastoreProvider.class).in(Singleton.class);
        // DI Cached
//        bind(RedisSerializer.class).to(JacksonJsonRedisSerializer.class);

        bind(String.class).annotatedWith(Names.named("cachePrefix")).toInstance("_dhapi_");


        bind(String.class).annotatedWith(Names.named("channelPrefix")).toInstance("_missphone_");
        bind(String.class).annotatedWith(Names.named("cooperSourcePrefix")).toInstance("_cooper_");

    }

//    @Provides
//    @Singleton
//    public ObjectPool<FtpService> provideObjectPool(Configuration configuration) {
//        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
//        conf.setMinIdle(configuration.getInt("ftp.pool.minidle", 1));
//        conf.setMaxIdle(configuration.getInt("ftp.pool.maxidle", 8)); // linux 会把长期不操作的连接给废弃掉，当你检测时候的时候是连接上状态，在发送数据的时候会抛出异常
//        conf.setMaxTotal(configuration.getInt("ftp.pool.maxtotal", 100));
//        conf.setMaxWaitMillis(configuration.getInt("ftp.pool.maxwaitmillis", 59 * 1000));
//        conf.setBlockWhenExhausted(configuration.getBoolean("ftp.pool.blockwhenwxhausted", true));
//        conf.setTestOnBorrow(true); // 每次匹配的对象做验证
//        conf.setTestWhileIdle(true);// 对闲置的池子中闲置的的对象做验证
//        conf.setTimeBetweenEvictionRunsMillis(configuration.getLong("ftp.pool.timebetweenevictionrunsmillis", 5 * 60 * 1000));
//        conf.setTestOnReturn(true);
//        // conf.setNumTestsPerEvictionRun();
//        GenericObjectPool pool = new GenericObjectPool<FtpService>(new FtpFactory(configuration));
//        pool.setConfig(conf);
//        return pool;
//
//    }
//
//    @Provides
//    @Singleton
//    public SqlSessionFactory provideSqlSessionFactory() {
//        final String config = "mybatis-config.xml";
//        try {
//            URL url = ConfigurationUtils.locate(CommonUtils.__CONF_DIR__, config);
//            LOGGER.info("mybatis config:" + url);
//            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(url.openStream());
//            LOGGER.info("mybatis environment:" + sqlSessionFactory.getConfiguration().getEnvironment().getId());
//            return sqlSessionFactory;
//        } catch (IOException e) {
//            LOGGER.error("failed to connect to db:" + config, e);
//        }
//        return null;
//    }

    // ---------------------------------------------------------------------------------------------------
    //
    // @Provides
    // public JedisPool getJedisPool(Configuration configuration) {
    // JedisPoolConfig poolConfig = new JedisPoolConfig();
    // poolConfig.setMaxActive(configuration.getInteger("redis.maxactive", 500));
    // poolConfig.setMaxIdle(configuration.getInteger("redis.maxidle", 50));
    // InetSocketAddress redisHost = CommonUtils.getAddress(configuration.getString("redis.address"));
    //
    // LOGGER.info("init jedis pool: {}", redisHost);
    // return new JedisPool(poolConfig, redisHost.getAddress().getHostAddress(),
    // redisHost.getPort(), 5000);
    // }

//    @Provides
//    @Singleton
//    public ShardedJedisPool provideShardedJedisPool(Configuration configuration) {
//        JedisPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxTotal(configuration.getInt("redis.maxactive", 500));
//        poolConfig.setMaxIdle(configuration.getInt("redis.maxidle", 50));
//        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//        String[] shardedAddress = configuration.getString("redis.sharded.address").split(",");
//        for (String address : shardedAddress) {
//            InetSocketAddress inetSocketAddress = CommonUtils.getAddress(address);
//            JedisShardInfo si = new JedisShardInfo(inetSocketAddress.getAddress().getHostAddress(), inetSocketAddress.getPort());
//            shards.add(si);
//        }
//        LOGGER.info("init sharded jedis pool:" + shards);
//        return new ShardedJedisPool(poolConfig, shards);
//    }

//    @Provides
//    @Singleton
//    public MissPhoneQueryer provideMissPhoneQueryer(ShardedJedisPool jedisPool, Configuration configuration, HttpClient httpClient, ReportService reportService, RedisService redisService,
//                                                    @Named("channelPrefix") String channelPrefix, @Named("cooperSourcePrefix") String cooperSourcePrefix) {
//        if (!configuration.getBoolean("api.query.missphone.switchon", true)) {
//            return null;
//        }
//        boolean subscribe = !configuration.getBoolean("api.query.missphone.publish", false);
//        return new MissPhoneQueryer(jedisPool, configuration, httpClient, reportService, redisService, channelPrefix, cooperSourcePrefix, subscribe,
//                LoggerFactory.getLogger("com.guogod.shopping.MissPhoneQueryHitLog"));
//    }
//
    @Provides
    @Singleton
    Configuration provideConfiguration() {
        return CommonUtils.getConfiguration(CommonUtils.__CONF_DIR__, "shopping.properties");
    }
//
//    @Provides
//    @Singleton
//    TagCategoryDAO provideTagCategoryDAO(Datastore datastore) {
//        TagCategoryDAO dao = new TagCategoryDAO(datastore);
//        dao.ensureIndexes();
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    CustomerScoreDAO provideCustomerScoreDAO(Datastore datastore) {
//        CustomerScoreDAO dao = new CustomerScoreDAO(datastore);
//        dao.ensureIndexes();
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    PhoneDAO providePhoneDAO(Datastore datastore) {
//        PhoneDAO dao = new PhoneDAO(datastore);
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    ReportTypeDAO provideReportTypeDAO(Datastore datastore) {
//        ReportTypeDAO dao = new ReportTypeDAO(datastore);
//        // dao.ensureIndexes();
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    ReportDAO provideReportDAO(Datastore datastore, ReportTypeDAO reportTypeDAO) {
//        ReportDAO dao = new ReportDAO(datastore, reportTypeDAO);
//        // dao.ensureIndexes();
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    WhiteListDAO provideWhiteListDAO(Datastore datastore) {
//        WhiteListDAO dao = new WhiteListDAO(datastore);
//        // dao.ensureIndexes();
//        return dao;
//    }
//
//    @Provides
//    @Singleton
//    WebSiteReportDAO provideWebSiteReportDAO(Datastore datastore) {
//        WebSiteReportDAO dao = new WebSiteReportDAO(datastore);
//        dao.ensureIndexes();
//        return dao;
//    }

    // ---------------------------------------------------------------------------------------------------
    @Provides
    @Singleton
    InitialContext provideInitialContext(Configuration configuration) {
        Properties prop = new Properties();
        Iterator<String> its = configuration.getKeys("java.naming");
        while (its.hasNext()) {
            String key = (String) its.next();
            prop.put(key, configuration.getString(key));
        }
        try {
            LOGGER.info("context environment:" + prop);
            return new InitialContext(prop);
        } catch (NamingException e) {
            LOGGER.error(e.toString(), e);
        }
        return null;
    }

//    @Provides
//    @Singleton
//    QueueConnection provideQueueConnection(Configuration configuration, InitialContext initialContext) {
//        try {
//            QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext.lookup("ConnectionFactory");
//            QueueConnection queueConnection = null;
//            if (configuration.getProperty("java.naming.security.principal") != null) {
//                queueConnection = queueConnectionFactory.createQueueConnection(configuration.getString("java.naming.security.principal"), configuration.getString("java.naming.security.credentials"));
//            } else {
//                queueConnection = queueConnectionFactory.createQueueConnection();
//            }
//            String clientID = "dianhua-customer-score@";
//            try {
//                clientID += InetAddress.getLocalHost();
//            } catch (Exception e) {
//                clientID += UUID.randomUUID().toString();
//            }
//            queueConnection.setClientID(clientID);
//            return queueConnection;
//        } catch (Exception e) {
//            LOGGER.error(e.toString(), e);
//        }
//        return null;
//    }
//
//    @Provides
//    @Singleton
//    MessageProducer provideQueueSender(Configuration configuration, QueueSession queueSession) {
//        try {
//            String queueName = configuration.getString("dianhua.cutomer.score.queue");
//            Destination destination;
//            destination = queueSession.createQueue(queueName);
//            MessageProducer producer;
//            producer = queueSession.createProducer(destination);
//            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
//            LOGGER.info("MQ queue sender with the name:{} created", queueName);
//            return producer;
//        } catch (Exception e) {
//            LOGGER.error(e.toString(), e);
//        }
//        return null;
//    }
//
//    @Provides
//    @Singleton
//    QueueSession provideQueueSession(Configuration configuration, QueueConnection queueConnection) {
//        try {
//            return queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//        } catch (JMSException e) {
//            LOGGER.warn(e.toString(), e);
//        }
//        return null;
//    }
//
//    @Named("_queue.dianhua.cutomer.score.queue")
//    @Provides
//    @Singleton
//    MessageConsumer provideSocreMessageQueueReciver(Configuration configuration, QueueSession queueSession) {
//        try {
//            Destination destination;
//            MessageConsumer consumer;
//            String queueName = configuration.getString("dianhua.cutomer.score.queue");
//            destination = queueSession.createQueue(queueName);
//            consumer = queueSession.createConsumer(destination);
//            LOGGER.info("MQ queue receiver with the name:{} created", queueName);
//            return consumer;
//        } catch (Exception e) {
//            LOGGER.error(e.toString(), e);
//        }
//        return null;
//    }
}
