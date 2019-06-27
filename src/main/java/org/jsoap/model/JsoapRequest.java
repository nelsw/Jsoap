package org.jsoap.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This class represents a JsoapObjectMapper request.
 * Most if not all the magic of JsoapObjectMapper is in the data and result table.
 */
@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JsoapRequest {

    /**
     * soap message url
     */
    @NotNull @URL String service;

    /**
     * soap message body (url)
     */
    @NotNull @URL String request;

    /**
     * Normally would default to UTF-8 but this is more convenient for github examples :/ sry guyz
     */
    String encoding = "ISO-8859-1";

    /**
     * Arguments, if any.
     */
    Map<String, String> data = new HashMap<>();

    /**
     * JsoapRequest headers
     */
    Map<String, String> headers = new HashMap<>();

    /**
     * Like JDBC ResultSet, or at least that's why I'm sticking with this name.
     */
    Map<String, Map<String, String>> table = new HashMap<>();

    /*
        convenience
     */
    public String postDataCharset() {
        return Charset.forName(encoding).name();
    }

    public JsoapRequest header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public JsoapRequest data(String key, String value) {
        data.put(key, value);
        return this;
    }

    public JsoapRequest table(String rootTag, String childTag, String fieldName) {
        table.putIfAbsent(Objects.toString(rootTag), new HashMap<>());
        table.get(rootTag).putIfAbsent(Objects.toString(childTag), fieldName);
        return this;
    }

    /*
        proxy stuff
     */
    String proxyType = "HTTP";
    String proxyHost;
    Integer proxyPort;
    Proxy proxy;

    public Proxy proxy() {
        if (proxy != null) {
            return proxy;
        }
        if (proxyHost == null && proxyPort == null) {
            return proxy = Proxy.NO_PROXY;
        }
        try {
            return proxy = new Proxy(Proxy.Type.valueOf(proxyType), new InetSocketAddress(proxyHost, proxyPort));
        } catch (Exception e) {
            // if this is the only error made by client
            // error will be thrown during HTTP request
            // which is more graceful than null, IMO.
            return proxy = Proxy.NO_PROXY;
        }
    }

}