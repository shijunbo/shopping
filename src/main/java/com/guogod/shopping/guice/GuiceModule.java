package com.guogod.shopping.guice;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.guogod.shopping.dao.IMobileDao;
import com.guogod.shopping.dao.MobileDaoImpl;
import com.guogod.shopping.http.CloseableHttpClientProvider;
import com.guogod.shopping.rest.api.RecommandMobileServerResource;
import com.guogod.shopping.service.AccessToken;
import com.guogod.shopping.service.ApiService;
import com.guogod.shopping.service.ApiServiceImpl;
import com.guogod.shopping.user.AgentServer;
import com.guogod.shopping.user.IAgentServer;
import com.guogod.shopping.utils.CommonUtils;
import com.mongodb.Mongo;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.http.client.HttpClient;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class GuiceModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceModule.class);

    @Override
    protected void configure() {
        // provider
        bind(SecureRandom.class).toProvider(SecureRandomProvider.class).in(Singleton.class);
        bind(PublicKey.class).toProvider(RSAPublicKeyProvider.class).in(Singleton.class);
        bind(PrivateKey.class).toProvider(RSAPrivateKeyProvider.class).in(Singleton.class);
        bind(HttpClient.class).toProvider(CloseableHttpClientProvider.class);
        bind(Mongo.class).toProvider(MongoProvider.class).in(Singleton.class);
        bind(Morphia.class).toProvider(MorphiaProvider.class).in(Singleton.class);
        bind(Datastore.class).toProvider(DatastoreProvider.class).in(Singleton.class);

        bind(RecommandMobileServerResource.class);
        bind(AccessToken.class).in(Singleton.class);
        bind(ApiService.class).to(ApiServiceImpl.class).in(Singleton.class);
        bind(IMobileDao.class).to(MobileDaoImpl.class).in(Singleton.class);
        bind(IAgentServer.class).to(AgentServer.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public SqlSessionFactory provideSqlSessionFactory() {
        final String config = "mybatis-config.xml";
        try {
            URL url = ConfigurationUtils.locate(CommonUtils.__CONF_DIR__, config);
            LOGGER.info("mybatis config:" + url);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(url.openStream());
            LOGGER.info("mybatis environment:" + sqlSessionFactory.getConfiguration().getEnvironment().getId());
            return sqlSessionFactory;
        } catch (IOException e) {
            LOGGER.error("failed to connect to db:" + config, e);
        }
        return null;
    }


    @Provides
    @Singleton
    Configuration provideConfiguration() {
        return CommonUtils.getConfiguration(CommonUtils.__CONF_DIR__, "shopping.properties");
    }

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
}
