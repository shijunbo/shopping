package com.guogod.shopping.rest.api;

import com.google.inject.Inject;
import com.guogod.shopping.datasource.DataSource;
import com.guogod.shopping.datasource.LineFileDataSource;
import com.guogod.shopping.service.AccessToken;
import com.guogod.shopping.service.ApiService;
import com.guogod.shopping.utils.Constant;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shijunbo on 2015/7/17.
 */
public class CreateMenuServerResource extends AbstractServerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateMenuServerResource.class);
    private ApiService apiService;
    private AccessToken accessToken;

    @Inject
    public CreateMenuServerResource(Configuration config, AccessToken accessToken, ApiService apiService) {
        this.configuration = config;
        this.apiService = apiService;
        this.accessToken = accessToken;
    }

    @Override
    public Representation processGetRequest(Form form) {
        Map<String, Object> success = null;
        try {
            Map<String,String> params = new HashMap<String,String>();
            params.put(Constant.ACCESS_TOKEN,accessToken.getAccessToken());
            String menu = loadMenuConfig();
            if( null != menu ) {
                success = apiService.createMenu(params, menu);
            }else{
                setStatus(Status.SERVER_ERROR_INTERNAL);
                LOGGER.error("load menu config error");
            }
        } catch (Exception e) {
            setStatus(Status.SERVER_ERROR_INTERNAL);
            LOGGER.warn("unknown error: {}", e);
        }
        return this.retrievalResponse.buildJsonResponse(success);
    }

    public String loadMenuConfig(){
        String menu = null;
        File menufile = new File("data", "data/menu.json");
        if (!menufile.exists()) {
            URL url = ConfigurationUtils.locate("data/menu.json");
            menufile = new File(url != null ? url.getFile() : "");
        }
        try {
            DataSource<String> dataSource = new LineFileDataSource(menufile);
            dataSource.open();
            if (dataSource.hasNext()) {
                menu = dataSource.next();
            }
            dataSource.close();
        } catch (Exception e) {
            LOGGER.info("=====加载Menu数据失败！=====");
        }
        return menu;
    }
}
