package com.buggycoder.domo.api.response;

import java.util.List;

/**
 * Created by shirish on 6/9/13.
 */
public class APIResponseCollection<T> extends APIResponse<List<T>> {

    private List<T> collection;

    @Override
    public List<T> getResponse() {
        return collection;
    }

    @Override
    public void setResponse(List<T> o) {
        collection = o;
    }
}
