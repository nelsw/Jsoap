package org.jsoap;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.experimental.UtilityClass;
import org.jsoap.model.JsoapRequest;
import org.jsoap.model.JsoapResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@UtilityClass
public class JsoapUtils {

    private Set<Map<String, String>> resultSet(JsoapRequest request, Document responseBody) {
        Set<Map<String, String>> resultSet = new HashSet<>();
        try {
            // no table
            if (request.table() == null || request.table().isEmpty()) {
                Element e = traverseToText(responseBody.children());
                if (e != null) {
                    Map<String, String> result = new HashMap<>();
                    result.put(e.tagName(), e.text());
                    resultSet.add(result);
                }
            } else {
                // has table
                for (Map.Entry<String, Map<String, String>> row : request.table().entrySet()) {
                    Elements elements = row.getKey().equals("nil") ? responseBody.getAllElements() : responseBody.getElementsByTag(row.getKey());
                    for (Element e : elements) {
                        Map<String, String> map = new HashMap<>();
                        // no map
                        if (row.getValue() == null || row.getValue().isEmpty()) {
                            map.put(row.getKey(), e.text());
                        } else {
                            // has map
                            for (Map.Entry<String, String> cell : row.getValue().entrySet()) {
                                Element c = e.selectFirst(cell.getKey().replaceAll(":", "|"));
                                if (c != null) {
                                    map.put(cell.getValue().equals("") ? cell.getKey() : cell.getValue(), c.text());
                                }
                            }
                        }
                        if (!map.isEmpty()) {
                            resultSet.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /**
     * recursion...
     *
     * @param elements
     * @return
     */
    private Element traverseToText(Elements elements) {
        for (Element element : elements) {
            if (element.hasText() && element.children().isEmpty()) {
                return element;
            } else if (!element.children().isEmpty()) {
                return traverseToText(element.children());
            }
        }
        return null;
    }

    public Function<JsoapRequest, JsoapResponse> response() {
        return jsoapRequest -> {
            int statusCode = 400;
            String body = "Error fetching request";
            try {
                Connection.Response request = Jsoup.connect(jsoapRequest.request()).method(Connection.Method.GET).execute();
                statusCode = request.statusCode();
                if (statusCode == 200) {
                    Document requestBody = Parser.xmlParser().parseInput(request.body(), jsoapRequest.request());
                    requestBody.outputSettings().prettyPrint(false);
                    if (!jsoapRequest.data().isEmpty()) {
                        for (Map.Entry<String, String> e : jsoapRequest.data().entrySet()) {
                            requestBody.selectFirst(e.getKey()).text(e.getValue());
                        }
                    }
                    if (jsoapRequest.headers().isEmpty()) {
                        jsoapRequest.header("Content-Length", String.valueOf(requestBody.toString().getBytes().length));
                        jsoapRequest.header("Content-Type", "text/xml;charset=" + jsoapRequest.encoding());
                        jsoapRequest.header("Accept-Encoding", "gzip,deflate");
                    }

                    Connection.Response response = Jsoup
                            .connect(jsoapRequest.service())
                            .headers(jsoapRequest.headers())
                            .postDataCharset(jsoapRequest.postDataCharset())
                            .requestBody(requestBody.toString())
                            .proxy(jsoapRequest.proxy())
                            .method(Connection.Method.POST)
                            .execute();
                    statusCode = response.statusCode();
                    Document responseBody = Parser.xmlParser().parseInput(response.body(), jsoapRequest.service());
                    responseBody.outputSettings().prettyPrint(false);
                    Document unescaped = Jsoup.parse(Parser.unescapeEntities(responseBody.toString(), true));
                    Set<Map<String, String>> resultSet = resultSet(jsoapRequest, unescaped);
                    body = json(resultSet);
                }
            } catch (Exception e) {
                body = e.getLocalizedMessage();
            }
            return new JsoapResponse(statusCode, body);
        };
    }

    public <T> T type(Class<T> typeClass, String body) {
        try {
            return JsoapObjectMapper.getInstance().getObjectMapper().readValue(body, typeClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String json(Object value) {
        try {
            return JsoapObjectMapper.getInstance().getObjectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}