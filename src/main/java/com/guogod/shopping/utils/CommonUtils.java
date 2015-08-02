package com.guogod.shopping.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.util.Series;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class CommonUtils {
    public static final String __CONF_DIR__ = "conf";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    public static final ObjectMapper JACKSON_OBJECT_MAPPER = new ObjectMapper();

    public static final Map<Class<?>, Class<?>> PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP = new HashMap<Class<?>, Class<?>>();

    public static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
    };
    /**
     *
     Primitive Type	Size	Minimum Value	Maximum Value	Wrapper Type
     char	   16-bit  	   Unicode 0	   Unicode 216-1	   Character
     byte	   8-bit  	   -128	   +127	   Byte
     short	   16-bit  	   -215
     (-32,768)	   +215-1
     (32,767)	   Short
     int	   32-bit  	   -231
     (-2,147,483,648)	   +231-1
     (2,147,483,647)	   Integer
     long	   64-bit  	   -263
     (-9,223,372,036,854,775,808)	   +263-1
     (9,223,372,036,854,775,807)	   Long
     float	   32-bit  	   32-bit IEEE 754 floating-point numbers	   Float
     double	   64-bit  	   64-bit IEEE 754 floating-point numbers	   Double
     boolean	   1-bit  	   true or false	   Boolean
     void	   -----  	   -----  	   -----  	   Void
     */
    static {
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(char.class, Character.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(byte.class, Byte.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(short.class, Short.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(int.class, Integer.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(long.class, Long.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(float.class, Float.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(double.class, Double.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(boolean.class, Boolean.class);
        PRIMITIVE_TYPE_TO_WRAPPER_TYPE_MAP.put(void.class, Void.class);
        JACKSON_OBJECT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    }

    public static String formatDateToGMT(Date date) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }

    public static Date parseDateInGMT(String gmt) {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            return df.parse(gmt);
        } catch (ParseException e) {
            LOGGER.warn("illegal gmt string:" + gmt, e);
        }
        return null;
    }

    public static String toJson(Object obj) {
        try {
            return JACKSON_OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            return e.toString();
        }
    }

    /**
     * Split a string in the form of "host:port host2:port" into a List of
     * InetSocketAddress instances suitable for instantiating a MemcachedClient.
     * <p/>
     * Note that colon-delimited IPv6 is also supported. For example: ::1:11211
     */
    public static List<InetSocketAddress> getAddresses(String s) {
        if (s == null) {
            throw new NullPointerException("Null host list");
        }
        if (s.trim().equals("")) {
            throw new IllegalArgumentException("No hosts in list:  ``" + s
                    + "''");
        }
        ArrayList<InetSocketAddress> addrs = new ArrayList<InetSocketAddress>();

        for (String hoststuff : s.split(",")) {
            int finalColon = hoststuff.lastIndexOf(':');
            if (finalColon < 1) {
                throw new IllegalArgumentException("Invalid server ``"
                        + hoststuff + "'' in list:  " + s);

            }
            String hostPart = hoststuff.substring(0, finalColon);
            String portNum = hoststuff.substring(finalColon + 1);

            addrs
                    .add(new InetSocketAddress(hostPart, Integer
                            .parseInt(portNum)));
        }
        assert !addrs.isEmpty() : "No addrs found";
        return addrs;
    }


    public static InetSocketAddress getAddress(String address) {
        int finalColon = address.lastIndexOf(':');
        if (finalColon < 1) {
            throw new IllegalArgumentException("Invalid address:"
                    + address);

        }
        String hostPart = address.substring(0, finalColon);
        String portNum = address.substring(finalColon + 1);

        return new InetSocketAddress(hostPart, Integer
                .parseInt(portNum));
    }

    @SuppressWarnings("unchecked")
    public static void copyParams(Form form, Map<String, Object> params) {
        Parameter param;
        Object currentValue = null;
        for (final Iterator<Parameter> iter = form.iterator(); iter.hasNext(); ) {
            param = iter.next();
            currentValue = params.get(param.getName());
            if (currentValue != null) {
                List<Object> values = null;
                if (currentValue instanceof List) {
                    // Multiple values already found for this entry
                    values = ((List<Object>) currentValue);
                } else {
                    // Second value found for this entry
                    // Create a list of values
                    values = new ArrayList<Object>();
                    values.add(currentValue);
                    params.put(param.getName(), values);
                }
                if (param.getValue() == null) {
                    values.add(Series.EMPTY_VALUE);
                } else {
                    values.add(param.getValue());
                }
            } else {
                if (param.getValue() != null) {
                    params.put(param.getName(), param.getValue());
                }
            }
        }
    }

    public static String toJson(Object obj, boolean pretty) {
        try {
            if (pretty) {
                return JACKSON_OBJECT_MAPPER.defaultPrettyPrintingWriter().writeValueAsString(obj);
            } else {
                return JACKSON_OBJECT_MAPPER.writeValueAsString(obj);
            }

        } catch (IOException e) {
            return e.toString();
        }
    }

    /**
     * @param str
     * @return
     * @throws java.io.IOException
     */
    public static byte[] zip(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return out.toByteArray();
    }

    public static final String getFileMD5(File file) throws IOException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return DigestUtils.md5Hex(inputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * @param bytes
     * @return
     * @throws java.io.IOException
     */
    public static String unzip(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString("GBK")
        return out.toString("UTF-8");
    }


    public static CompositeConfiguration getConfiguration(String confDir,
                                                          String prop) {
        CompositeConfiguration config = new CompositeConfiguration();
        File file = new File(confDir + "/" + prop);
        FileChangedReloadingStrategy reloadingStrategy = new FileChangedReloadingStrategy();
        reloadingStrategy.setRefreshDelay(10000);// 10s
        URL url = null;
        try {
            if (file.exists()) {
                url = file.toURI().toURL();
            } else {
                url = org.apache.commons.configuration.ConfigurationUtils
                        .locate(prop);

            }
            LOGGER.info("loading conf from:" + url);
            PropertiesConfiguration fileConfiguraton = new PropertiesConfiguration(
            );

            fileConfiguraton.setEncoding("utf-8");
            fileConfiguraton.setDelimiterParsingDisabled(true);
            fileConfiguraton.load(url);
            fileConfiguraton.setReloadingStrategy(reloadingStrategy);
            config.addConfiguration(fileConfiguraton);

        } catch (Exception e) {
            LOGGER.error("Failed to load config:" + prop, e);
        }
        return config;
    }

    /**
     * Log配置文件名必须为logback.xml
     *
     * @param confDir
     */
    public static void loadLogbackConfiguration(String confDir) {
        try {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            File file = new File(confDir + "/logback.xml");
            if (file.exists()) {
                configurator.doConfigure(file);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("loading logback conf from:{}", file.getAbsolutePath());
                }
            } else {
                URL url = org.apache.commons.configuration.ConfigurationUtils.locate("logback.xml");
                configurator.doConfigure(url);
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("loading logback conf from:{}", url);
                }
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (JoranException e) {
            LOGGER.error("failed to load logback config from:" + confDir, e);
        }

    }
}
