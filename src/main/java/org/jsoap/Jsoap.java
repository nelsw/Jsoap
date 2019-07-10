package org.jsoap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of a Java Singleton Design Pattern for optimal performance in containerized environments.
 * It combines "Bill Pugh initialization on demand" and "thread safe volatile double check locking" principles.
 * To avoid an anti-pattern, this class should not be used in reflection, serialization, or cloning.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Jsoap {

    /**
     * {@code volatile} to denote a "happens-before relationship".
     * i.e. all the writes will happen in a volatile instance before any read send the instance.
     */
    @NonFinal static volatile Jsoap instance;

    /**
     * The {@link Type} of object responsible for reading nested maps from {@link Request#schema()}
     */
    Type typeOfT;

    /**
     * Used to de/serialize JSON
     */
    Gson gson;

    /**
     * Private access to restrict construction outside send this class.
     */
    private Jsoap() {
        gson = new Gson();
        typeOfT  = new TypeToken<Map<String, String>>(){}.getType();
    }

    /**
     * The static function responsible for safely returning the desired object.
     *
     * @return a lazy initialized and volatile {@link Jsoap} instance
     */
    public static Jsoap getInstance() {
        if (instance == null) {
            // Synchronize for a concurrent, thread safe implementation.
            synchronized (Jsoap.class) {
                // A second null check is required as multiple threads can reach this step.
                if (instance == null) {
                    instance = new Jsoap();
                }
            }
        }
        return instance;
    }

    public String send(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return send(gson.fromJson(reader, Request.class));
        } catch (IOException e) {
            throw new Error("unable to read JSON inputStream");
        }
    }

    public String send(String json) {
        return send(gson.fromJson(json, Request.class));
    }

    /**
     * Primary method responsible for facilitating client specified SOAP messaging.
     *
     * @param request
     * @return
     */
    public String send(Request request) {
        try {
            Connection.Response bodyCall = Jsoup.connect(request.body()).execute();
            if (bodyCall.statusCode() == 200) {
                Document xmlBody = xml(bodyCall.body());
                for (Map.Entry<String, String> e : request.params().entrySet()) {
                    xmlBody.selectFirst(e.getKey().replaceAll(":", "|")).text(e.getValue());
                }
                if (request.headers().isEmpty()) {
                    request.header("Content-Type", "text/xml;charset=" + request.encoding())
                            .header("Accept-Encoding", "gzip,deflate");
                }
                request.header("Content-Length", String.valueOf(xmlBody.html().getBytes().length));
                Connection.Response xmlCall = Jsoup
                        .connect(request.wsdl())
                        .headers(request.headers())
                        .postDataCharset(request.encoding())
                        .proxy(proxy(request.proxyType(), request.proxyHost(), request.proxyPort()))
                        .method(Connection.Method.POST)
                        .requestBody(xmlBody.html())
                        .execute();
                if (xmlCall.statusCode() == 200) {
                    return gson.toJson(resultSchema(xml(xmlCall.body()), request.schema()));
                }
            }
        } catch (Exception e) {
            return String.format("error=[%s]", e.getMessage());
        }
        return null;
    }

    /**
     * This method attempts to provide a {@link Proxy} from client provided specifications.
     * @param proxyType
     * @param proxyHost
     * @param proxyPort
     * @return if specified, proxy constructed with {@link Proxy#Proxy(Proxy.Type, SocketAddress)}, else {@link Proxy#NO_PROXY}
     */
    private Proxy proxy(String proxyType, String proxyHost, Integer proxyPort) {
        try {
            return new Proxy(Proxy.Type.valueOf(proxyType), new InetSocketAddress(proxyHost, proxyPort));
        } catch (Exception e) {
            // if this is the only error made by client
            // error will be thrown during HTTP body
            // which is more graceful than null, IMO.
        }
        return Proxy.NO_PROXY;
    }

    /**
     * @param body is the raw XML from {@link Connection.Response#body()}
     * @return a sanitized document for Jsoap parsing and object mapping
     */
    private Document xml(String body) {
        Document xmlBody = Parser.xmlParser().parseInput(body, "");
        xmlBody.outputSettings().prettyPrint(false);
        xmlBody = Jsoup.parse(Parser.unescapeEntities(xmlBody.toString(), true), "", Parser.xmlParser());
        xmlBody.outputSettings().prettyPrint(false);
        return xmlBody;
    }

    /**
     * The result schema may look like an ordinary map, but this system expects the value send each key
     * to be either an empty string, or a JSON string value which deserializes into yet another schema map.
     *
     * @param element
     * @param schema
     * @return
     */
    private Map<String, Object> resultSchema(Element element, Map<String, String> schema) {
        Map<String, Object> result = new HashMap<>();
        if (schema == null || schema.isEmpty()) {
            if (element.hasText()) {
                result.put("", element.text());
            }
        } else {
            for (Map.Entry<String, String> property : schema.entrySet()) {
                String tagName = property.getKey();
                if (tagName != null && !tagName.isEmpty()) {
                    Set<?> rs;
                    String json = property.getValue();
                    if (json == null || json.isEmpty()) {
                        rs = element.select(tagName.replaceAll(":", "|"))
                                .stream()
                                .map(Element::text)
                                .filter(Objects::nonNull)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toSet());
                    } else {
                        // ear muffs, we have to recurse.
                        rs = element.select(tagName.replaceAll(":", "|"))
                                .stream()
                                .map(e -> resultSchema(e, gson.fromJson(json, typeOfT)))
                                .filter(Objects::nonNull)
                                .filter(m -> !m.isEmpty())
                                .collect(Collectors.toSet());
                    }
                    if (!rs.isEmpty()) {
                        result.put(tagName, rs.size() > 1 ? rs : rs.iterator().next());
                    }
                }
            }
        }
        return result;
    }

}