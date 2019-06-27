package org.jsoap.model;

import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Value
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JsoapResponse {

    Integer code;
    String body;

}