package com.buggycoder.domo.api.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.lib.JsonManager;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.RequestManager;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.j256.ormlite.dao.Dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by shirish on 5/9/13.
 */
public class SupporteeAPI {

    static final String API_ROOT = "http://buggycoder.com:4000/api/v1/organizations/%s/advicerequest";


    public static void newAdviceRequest(String orgURL, String orgCode, String adviceRequest) throws UnsupportedEncodingException {

        String url = String.format(API_ROOT, orgURL) + "?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET);

        ObjectNode reqBody = JsonManager.getMapper().createObjectNode();
        reqBody.put("adviceRequest", adviceRequest);

        APIRequest apiRequest = new APIRequest<Organization, APIResponse<AdviceRequest>>(
                Request.Method.POST,
                url,
                reqBody,
                AdviceRequest.class,
                new Response.Listener<APIResponse<AdviceRequest>>() {
                    @Override
                    public void onResponse(APIResponse<AdviceRequest> response) {
                        if (response.hasError) {
                            Logger.d("Error: " + response.errors.toString());
                            return;
                        }

                        try {
                            Dao<AdviceRequest, String> daoOrg =
                                    DatabaseHelper
                                            .getDaoManager()
                                            .getDao(AdviceRequest.class);

                            AdviceRequest ar = response.getResponse();
                            Dao.CreateOrUpdateStatus status = daoOrg.createOrUpdate(ar);
                            Logger.d(status.isCreated() + " | " + status.isUpdated());
                            Logger.dump(ar);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.d(volleyError.toString());
                    }
                }
        );

        apiRequest.setPath("advicerequest");

        RequestManager.getRequestQueue().add(apiRequest);
    }

    public static void fetchAdviceRequest(String orgURL, String orgCode, final String advicerequestId, String token) throws UnsupportedEncodingException {

        String url = String.format(API_ROOT, orgURL) + "/" + advicerequestId
                + "?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET)
                + "&token=" + URLEncoder.encode(token, APIRequest.PROTOCOL_CHARSET);
        Logger.d("url: %s", url);

        APIRequest apiRequest = new APIRequest<Organization, APIResponse<AdviceRequest>>(
                Request.Method.GET,
                url,
                null,
                AdviceRequest.class,
                new Response.Listener<APIResponse<AdviceRequest>>() {
                    @Override
                    public void onResponse(APIResponse<AdviceRequest> response) {
                        if (response.hasError) {
                            Logger.d("Error: " + response.errors.toString());
                            return;
                        }

                        try {
                            Dao<AdviceRequest, String> daoAr =
                                    DatabaseHelper
                                            .getDaoManager()
                                            .getDao(AdviceRequest.class);

                            Dao<Advice, String> daoAdv =
                                    DatabaseHelper
                                            .getDaoManager()
                                            .getDao(Advice.class);

                            AdviceRequest ar = response.getResponse();
                            Dao.CreateOrUpdateStatus status = daoAr.createOrUpdate(ar);
                            Logger.d(status.isCreated() + " | " + status.isUpdated());

                            Collection<Advice> adviceList = ar.getResponses();
                            for (Advice a : adviceList) {
                                a.setAdviceRequest(ar);
                                daoAdv.createIfNotExists(a);
                            }

                            AdviceRequest ar2 = daoAr.queryForId(advicerequestId);
                            Logger.dump(ar2);
                            Logger.d("Advice count: " + ar2.getResponses().size());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.d(volleyError.toString());
                    }
                }
        );

        apiRequest.setPath("advicerequest");

        RequestManager.getRequestQueue().add(apiRequest);

    }

    public static void markHelpful(String orgURL, String orgCode, String advicerequestId, String adviceId, String token) {

    }

    public static void thankSupporter(String orgURL, String orgCode, String advicerequestId, String adviceId, String token) {

    }
}
