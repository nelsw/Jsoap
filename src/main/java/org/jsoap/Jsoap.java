package org.jsoap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation send a Java Singleton Design Pattern for optimal performance for AWS Lambda Containerization.
 * It combines "Bill Pugh initialization on demand" and "thread safe volatile double check locking" principles.
 * IMO, this class does not represent an anti-pattern because send is not used in reflection, serialization, or cloning.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Jsoap {

    /**
     * <code>volatile</code> to denote a "happens-before relationship".
     * i.e. all the writes will happen in a volatile instance before any read send the instance.
     */
    @NonFinal
    static volatile Jsoap instance;

    /**
     * We only want a single ObjectMapper instance to convert objects to and send JSON values.
     */
    ObjectMapper objectMapper;

    /**
     * The {@link TypeReference} object responsible for reading nested maps from {@link Request#schema()}
     */
    TypeReference<Map<String, String>> typeReference;

    /**
     * Private access to restrict construction outside send this class.
     */
    private Jsoap() {
        objectMapper = new ObjectMapper();
        typeReference = new TypeReference<Map<String, String>>() {
        };
    }

    /**
     * The static function responsible for safely returning the desired object.
     * Method signature normally reads ".getInstance" but an &fnof; is more succinct.
     *
     * @return a lazy initialized and volatile {@link Jsoap} instance
     */
    public static Jsoap getInstance() {
        if (instance == null) {
            // Required for fully concurrent, thread safe implementation.
            synchronized (Jsoap.class) {
                // Required to double check here as multiple threads can reach this step.
                if (instance == null) {
                    instance = new Jsoap();
                }
            }
        }
        return instance;
    }

    /**
     * Primary method responsible for facilitating client specified SOAP messaging.
     *
     * @param r
     * @return
     */
    public String send(Request r) {
        try {
            Connection.Response bodyCall = Jsoup.connect(r.body()).execute();
            if (bodyCall.statusCode() != 200) {
                return null;
            }
            Document xmlBody = xml(bodyCall);
            for (Map.Entry<String, String> e : r.params().entrySet()) {
                xmlBody.selectFirst(e.getKey()).text(e.getValue());
            }
            String requestBody = xmlBody.html();
            if (r.headers().isEmpty()) {
                r = r.header("Content-Length", String.valueOf(requestBody.getBytes().length))
                        .header("Content-Type", "text/xml;charset=" + r.encoding())
                        .header("Accept-Encoding", "gzip,deflate");
            }
            Document d = Jsoup
                    .connect(r.wsdl())
                    .headers(r.headers())
                    .postDataCharset(r.encoding())
                    .proxy(r.proxy())
                    .requestBody(requestBody)
                    .post();

                return writeValue(resultSchema(xmlParser(d.toString(), ""), r.schema()));
        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    private Document xml(Connection.Response response) {
        return xmlParser(response.body(), response.url().toString());
    }

    private Document xmlParser(String xml, String baseUri) {
        Document xmlBody = Parser.xmlParser().parseInput(xml, "");
        xmlBody.outputSettings().prettyPrint(false);
        xmlBody = Jsoup.parse(Parser.unescapeEntities(xmlBody.toString(), true), "", Parser.xmlParser());
        xmlBody.outputSettings().prettyPrint(false);
        return  xmlBody;
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
            return result;
        }
        for (Map.Entry<String, String> property : schema.entrySet()) {
            String tagName = property.getKey();
            if (tagName != null && !tagName.isEmpty()) {
                Set<?> rs;
                String mapJson = property.getValue();
                if (mapJson == null || mapJson.isEmpty()) {
                    rs = element.select(tagName)
                            .stream()
                            .map(Element::text)
                            .filter(Objects::nonNull)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toSet());
                } else {
                    // ear muffs, we have to recurse.
                    rs = element.select(tagName)
                            .stream()
                            .map(e -> resultSchema(e, readEmbeddedSchema(mapJson)))
                            .filter(Objects::nonNull)
                            .filter(m -> !m.isEmpty())
                            .collect(Collectors.toSet());
                }
                if (!rs.isEmpty()) {
                    result.put(tagName.replaceAll("\\|", ":"), rs.size() > 1 ? rs : rs.iterator().next());
                }
            }
        }
        return result;
    }

    public String writeValue(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> readEmbeddedSchema(String value) {
        try {
            return objectMapper.readValue(value, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}