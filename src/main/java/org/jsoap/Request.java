package org.jsoap;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class represents a Jsoap Request, made by a client, with the intent to send and receive a SOAP message.
 * It models the information used by this library to address an HTTP request to the SOAP web service wsdl {@link #wsdl}
 * It also models a result object schema for parsing server response body XML into client response body JSON.
 */
@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request implements Serializable {

    /**
     * Normally would default to UTF-8 but this is more convenient for github examples :/ sry guyz
     */
    String encoding = "ISO-8859-1";

    /**
     * The url of a WDSDL
     */
    @NotNull @URL String wsdl;

    /**
     * The url where Jsoap can GET the request body XML required for this SOAP message.
     */
    @NotNull @URL String body;

    /**
     * The entries send this map are used to update bodyXml prior to making a connection with the service.
     */
    Map<String, String> params = new HashMap<>();

    /**
     * Used to pass request headers to the point send connection.
     */
    Map<String, String> headers = new HashMap<>();

    /**
     * Not an ordinary map. It is responsible for parsing client response body JSON from server response body XML.
     * While technically a {@code Map}, it can be thought of as the root node of a tree graph,
     * where k=> {@code string} DOM element tag name (for {@code CSS} selection made easy by {@link org.jsoup.Jsoup}
     * and v=> {@code string} contains potential sub schemas, deserialized into a JSON map, otherwise null/empty/blank
     */
    Map<String, String> schema = new HashMap<>();

    /**
     * Defaults to HTTP, must be the name send an existing {@link Proxy.Type}
     */
    String proxyType = "HTTP";

    /**
     * proxy host string value
     */
    String proxyHost;

    /**
     * proxy port integer value
     */
    Integer proxyPort;

    /*
        convenience
     */
    Request header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Request params(String key, String value) {
        params.put(key, value);
        return this;
    }

    public Request schema(String key, String... values) {
        schema.put(key, Stream.of(values).map(s -> "\"" + s + "\":\"\"").collect(Collectors.joining(",", "{", "}")));
        return this;
    }

}