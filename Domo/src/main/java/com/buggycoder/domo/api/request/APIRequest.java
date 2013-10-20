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
import com.buggycoder.domo.lib.Logger;
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
    public static final String PROTOCOL_CONTENT_TYPE =
            String.format("application/json; charset=%s", PROTOCOL_CHARSET);
    private final JsonNode mRequestBody;
    ResponseHandler responseHandler;
    ErrorHandler errorHandler;

    public APIRequest(int method,
                      String url,
                      JsonNode requestBody,
                      ResponseHandler<E> rp,
                      ErrorHandler ep) {
        super(method, url, newErrorListener(rp, ep));
        responseHandler = rp;
        errorHandler = ep;
        mRequestBody = requestBody;
    }

    private static <T> Response.ErrorListener newErrorListener(final ResponseHandler responseHandler, final ErrorHandler errorHandler) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                APIResponse apiResponse;

                if (responseHandler.isMapToCollection()) {
                    apiResponse = new APIResponseCollection<T>();
                } else {
                    apiResponse = new APIResponse<T>();
                }

                if (volleyError instanceof ServerError) {
                    try {
                        JsonNode jsonRes = JsonManager.getUnsafeMapper().readTree(volleyError.networkResponse.data);
                        Logger.d("Response: " + jsonRes.toString());
                        errorHandler.processError(jsonRes, apiResponse);
                        responseHandler.onResponse(apiResponse);
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                        throw e;
                    }

                } else if (volleyError instanceof TimeoutError || volleyError instanceof NoConnectionError) {
                    apiResponse.hasError = true;
                    List<String> errors = new ArrayList();
                    errors.add((volleyError instanceof TimeoutError) ? "Network timed out" : "No connectivity");
                    apiResponse.errors = errors;
                }

                errorHandler.onErrorResponse(volleyError);
            }
        };
    }

    @Override
    protected void deliverResponse(E response) {
        responseHandler.onResponse(response);
    }

    @Override
    protected Response<E> parseNetworkResponse(NetworkResponse response) {
        try {
            E result = (E) responseHandler.processResponse(response.data, errorHandler);
            return Response.success(result, getCacheEntry());
        } catch (IOException e) {
            VolleyLog.d("IOException %s", e.getMessage());
            Response.error(new VolleyError(e));
        }
        return null;
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

    public static abstract class ResponseHandler<E extends APIResponse<?>> implements Response.Listener<E> {
        private Class responseType;
        private boolean mapToCollection;
        private List<String> path;

        public ResponseHandler(Class rt, boolean mp) {
            responseType = rt;
            mapToCollection = mp;
        }

        public Class getResponseType() {
            return responseType;
        }

        public void setResponseType(Class responseType) {
            this.responseType = responseType;
        }

        public boolean isMapToCollection() {
            return mapToCollection;
        }

        public void setMapToCollection(boolean mapToCollection) {
            this.mapToCollection = mapToCollection;
        }

        public void setPath(String path) {
            if (path == null) {
                return;
            }
            this.path = Arrays.asList(path.split("\\."));
        }

        public JsonNode getResponseNode(JsonNode jsonRes) throws IOException {
            JsonNode response = jsonRes;
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
            return response;
        }

        public <T> APIResponse processResponse(byte[] responseBody, ErrorHandler errorHandler) throws IOException {
            ObjectMapper mapper = JsonManager.getUnsafeMapper();
            JsonNode jsonRes = mapper.readTree(responseBody);
            APIResponse apiResponse = processJson(jsonRes);
            errorHandler.processError(jsonRes, apiResponse);
            return apiResponse;
        }


        public <T> APIResponse processJson(JsonNode jsonRes) throws IOException {
            ObjectMapper mapper = JsonManager.getUnsafeMapper();
            JsonNode response = getResponseNode(jsonRes);
            try {
                Logger.d(jsonRes.toString());
            } catch (Exception e) {

            }
            APIResponse apiResponse;

            if (isMapToCollection()) {
                apiResponse = new APIResponseCollection<T>();
                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, getResponseType());
                apiResponse.setResponse(mapper.convertValue(response, type));
            } else {
                apiResponse = new APIResponse<T>();
                apiResponse.setResponse(mapper.convertValue(response, getResponseType()));
            }

            return apiResponse;
        }
    }

    public static abstract class ErrorHandler<E extends APIResponse<?>> implements Response.ErrorListener {

        public void processError(JsonNode jsonRes, E apiResponse) {

            apiResponse.rawResponse = jsonRes;

            if (apiResponse.errors != null && apiResponse.errors.size() > 0) {
                // errors already parsed by subclass. skip parsing.
                return;
            }

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

    }
}