package com.guogod.shopping.rest;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

import com.guogod.shopping.rest.api.CreateMenuServerResource;
import com.guogod.shopping.service.AccessToken;
import org.apache.commons.configuration.Configuration;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.guogod.shopping.guice.GuiceModule;
import com.guogod.shopping.rest.guice.FinderFactory;
import com.guogod.shopping.rest.guice.RestletGuice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.guogod.shopping.utils.CommonUtils;
import com.guogod.shopping.signal.SignalManager;
import com.guogod.shopping.rest.api.RecommandMobileServerResource;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class ShopApplication extends org.restlet.Application {
    private static Logger LOGGER = LoggerFactory.getLogger(ShopApplication.class);
    private Injector injector = null;

    public ShopApplication(Injector injector) {
        this.injector = injector;
    }

    public ShopApplication(Context context) {
        super(context);
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        LOGGER.info("stopping...");
        super.stop();
        LOGGER.info("stopped");
    }

    @Override
    public Restlet createInboundRoot() {
        FinderFactory ff = injector.getInstance(FinderFactory.class);
        Router router = new Router(getContext());
        router.attach("/shop.php", ff.finder(RecommandMobileServerResource.class));
        router.attach("/shop/menu/create", ff.finder(CreateMenuServerResource.class));
        return router;
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        CommonUtils.loadLogbackConfiguration(CommonUtils.__CONF_DIR__);
        LOGGER.info("TotalMemory:" + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " M");
        final org.restlet.Component component = new org.restlet.Component();
        List<Module> modules = new ArrayList<Module>();
        modules.add(new GuiceModule());
        Injector injector = RestletGuice.createInjector(modules);
        Configuration configuration = injector.getInstance(Configuration.class);
        injector.getInstance(AccessToken.class);
        Application dialApp = new ShopApplication(injector);
        component.getDefaultHost().attach(dialApp);
        component.getClients().add(Protocol.HTTP);
//        component.getClients().add(Protocol.FILE);
        Server server = null;
        if ( configuration.getBoolean("server.deploy.alibaba", false) ) {
            server = component.getServers().add(Protocol.HTTP, configuration.getString("server.public.ip", "123.57.240.242"), configuration.getInt("server.port"));
        }else{
            server = component.getServers().add(Protocol.HTTP, configuration.getInt("server.port"));
        }
        // jetty config
        server.getContext().getParameters().add("minThreads", "50");
        server.getContext().getParameters().add("maxThreads", "1024");
        server.getContext().getParameters().add("acceptorThreads", "4");
        server.getContext().getParameters().add("gracefulShutdown", "5000");
        server.getContext().getParameters().add("useForwardedForHeader", "true");
        component.start();
        LOGGER.info("Sever started on " + Inet4Address.getLocalHost().getHostAddress() + ":" + configuration.getInt("server.port"));
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    component.stop();
                    LOGGER.info("gracefully shutdown shopping system");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        SignalManager.install();
    }
}
