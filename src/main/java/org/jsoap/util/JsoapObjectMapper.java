package org.jsoap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import lombok.experimental.NonFinal;

/**
 * Implementation of a Java Singleton Design Pattern for optimal performance for AWS Lambda Containerization.
 * It combines "Bill Pugh initialization on demand" and "thread safe volatile double check locking" principles.
 * IMO, this class does not represent an anti-pattern because it is not used in reflection, serialization, or cloning.
 */
@Value
public class JsoapObjectMapper {

    /**
     * <code>volatile</code> to denote a "happens-before relationship".
     * i.e. all the writes will happen in a volatile instance before any read of the instance.
     */
    @NonFinal
    static volatile JsoapObjectMapper instance;

    /**
     * We only want a single ObjectMapper instance to convert objects to and from JSON values.
     */
    ObjectMapper objectMapper;

    /**
     * Private access to restrict construction outside of this class.
     */
    private JsoapObjectMapper() {
        objectMapper = new ObjectMapper();
    }

    /**
     * The static function responsible for safely returning the desired object.
     * Method signature normally reads ".getInstance" but an &fnof; is more succinct.
     *
     * @return a lazy initialized and volatile {@link JsoapObjectMapper} instance
     */
    public static JsoapObjectMapper getInstance() {
        if (instance == null) {
            // Required for fully concurrent, thread safe implementation.
            synchronized (JsoapObjectMapper.class) {
                // Required to double check here as multiple threads can reach this step.
                if (instance == null) {
                    instance = new JsoapObjectMapper();
                }
            }
        }
        return instance;
    }

}