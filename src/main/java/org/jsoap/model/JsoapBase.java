package org.jsoap.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;

@Data
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PACKAGE)
public class JsoapBase implements JsoapAgent {


    @Override
    public JsoapAgent url(String url) {
        return null;
    }
}
