package com.buggycoder.domo.lib;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by shirish on 9/7/13.
 */
public class JsonManager {

    private static ObjectMapper mapper = null;
    private static ObjectMapper unsafeMapper = null;

    private JsonManager() {
        mapper = new ObjectMapper();
    }

    public static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        return mapper;
    }

    public static ObjectMapper getUnsafeMapper() {
        if (unsafeMapper == null) {
            unsafeMapper = new ObjectMapper();
        }
        unsafeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return unsafeMapper;
    }
}
