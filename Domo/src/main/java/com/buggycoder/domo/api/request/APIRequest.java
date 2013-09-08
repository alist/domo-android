package com.buggycoder.domo.api.request;


import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.APIResponseCollection;
import com.buggycoder.domo.lib.JsonManager;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class APIRequest<E extends APIResponse<?>> extends Request<E> {

    public static final String PROTOCOL_CHARSET = "utf-8";
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private final JsonNode mRequestBody;
    private final Response.Listener<E> mListener;
    private Class mClass;
    private ObjectMapper mapper;
    private List<String> path;
    private boolean mMapToCollection;


    public <T> APIRequest(int method,
                          String url,
                          JsonNode requestBody,
                          Class<T> responseType,
                          boolean mapToCollection,
                          Response.Listener<E> listener,
                          Response.ErrorListener errorListener, boolean strict) {

        super(method, url, getErrorListener(responseType, mapToCollection, listener, errorListener));

        mListener = listener;
        mRequestBody = requestBody;
        mClass = responseType;
        mMapToCollection = mapToCollection;
        mapper = strict ? JsonManager.getMapper() : JsonManager.getUnsafeMapper();
    }

    public <T> APIRequest(int method,
                          String url,
                          JsonNode requestBody,
                          Class<T> responseType,
                          Response.Listener<E> listener,
                          Response.ErrorListener errorListener
    ) {
        this(method, url, requestBody, responseType, false, listener, errorListener, false);
    }

    public <T> APIRequest(int method,
                          String url,
                          JsonNode requestBody,
                          Class<T> responseType,
                          boolean mapToCollection,
                          Response.Listener<E> listener,
                          Response.ErrorListener errorListener
    ) {
        this(method, url, requestBody, responseType, mapToCollection, listener, errorListener, false);
    }

    @Override
    protected void deliverResponse(E response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<E> parseNetworkResponse(NetworkResponse response) {
        try {
            E result = (E) parseResponse(response.data, mapper, path, mMapToCollection, mClass);
            return Response.success(result, getCacheEntry());
        } catch (IOException e) {
            VolleyLog.d("IOException %s", e.getMessage());
            Response.error(new VolleyError(e));
        }
        return null;
    }

    private static <T> APIResponse parseResponse(byte[] responseBody, ObjectMapper mapper, List<String> path, boolean mapToCollection, Class<T> clazz) throws IOException {
        JsonNode jsonRes = mapper.readTree(responseBody);
        JsonNode response = jsonRes.path("response");

        if (path != null) {
            Iterator<String> itrPath = path.iterator();
            String subPath;
            while (itrPath.hasNext()) {
                subPath = itrPath.next();
                if (response.path(subPath).isMissingNode()) {
                    throw new IOException("Incorrect path (" + subPath + "): " + path.toString());
                }
                response = response.path(subPath);
            }
        }

        APIResponse apiResponse;

        if (mapToCollection) {
            apiResponse = new APIResponseCollection<T>();
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            apiResponse.setResponse(mapper.convertValue(response, type));
        } else {
            apiResponse = new APIResponse<T>();
            apiResponse.setResponse(mapper.convertValue(response, clazz));
        }

        parseMetaAndErrors(jsonRes, apiResponse);

        return apiResponse;
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.toString().getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }

    public void setPath(String path) {
        if (path == null) {
            return;
        }
        this.path = Arrays.asList(path.split("\\."));
    }


    private static void parseMetaAndErrors(JsonNode jsonRes, APIResponse apiResponse) {

        apiResponse.meta = jsonRes.path("meta");
        apiResponse.rawResponse = jsonRes;

        JsonNode statusCode = jsonRes.path("meta").path("statusCode");
        apiResponse.hasError = (!statusCode.isMissingNode() && statusCode.asInt() >= 400);

        if (!jsonRes.path("errors").isMissingNode()) {
            ArrayNode errors = (ArrayNode) jsonRes.path("errors");
            if (errors != null && errors.isArray() && errors.size() > 0) {
                apiResponse.hasError = true;
                apiResponse.errors = new ArrayList<String>();

                Iterator<JsonNode> errElems = errors.elements();
                while (errElems.hasNext()) {
                    apiResponse.errors.add(errElems.next().asText());
                }
            }
        }
    }

    private static <S> Response.ErrorListener getErrorListener(Class<S> responseType, final boolean mapToCollection, final Response.Listener listener, final Response.ErrorListener errorListener) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                APIResponse apiResponse;

                if (mapToCollection) {
                    apiResponse = new APIResponseCollection<S>();
                } else {
                    apiResponse = new APIResponse<S>();
                }

                if (volleyError instanceof ServerError) {
                    try {
                        JsonNode jsonRes = JsonManager.getUnsafeMapper().readTree(volleyError.networkResponse.data);
                        parseMetaAndErrors(jsonRes, apiResponse);
                        listener.onResponse(apiResponse);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    apiResponse.hasError = true;
                    List<String> errors = new ArrayList();
                    errors.add((volleyError instanceof TimeoutError) ? "Network timed out" : "No connectivity");
                    apiResponse.errors = errors;
                }

                errorListener.onErrorResponse(volleyError);
            }
        };
    }
}