package org.jsoap.model;

import java.net.Proxy;
import java.util.Map;
import java.util.function.Supplier;

public interface JsoapAgent  {

    JsoapAgent url(String url);

    interface AgentRequest extends Supplier<JsoapRequest> {

        String service();

        String request();

        String encoding();

        Map<String, String> data();

        Map<String, String> headers();

        Map<String, Map<String, String>> table();

        Proxy proxy();

        String json();

        String postDataCharset();

        JsoapRequest header(String key, String value);

        JsoapRequest data(String key, String value);
    }

    interface AgentResponse {

        Integer code();

        String body();

        <T> T type(Class<T> type);

    }


}