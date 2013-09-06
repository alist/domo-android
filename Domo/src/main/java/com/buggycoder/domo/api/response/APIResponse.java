package com.buggycoder.domo.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * Created by shirish on 5/9/13.
 */
public class APIResponse<T> {

    @JsonIgnore
    public JsonNode meta;
    @JsonIgnore
    public JsonNode rawResponse;

    public boolean hasError;
    public List<String> errors;

    private T resource;

    public T getResponse() {
        return resource;
    }

    public void setResponse(T o) {
        resource = o;
    }

}
