package org.jsoap.model;

import lombok.Value;
import lombok.experimental.Accessors;
import org.jsoap.JsoapUtils;

@Value
@Accessors(fluent = true)
public class JsoapResponse {

    Integer code;
    String body;

}