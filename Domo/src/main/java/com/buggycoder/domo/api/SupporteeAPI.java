package com.buggycoder.domo.api;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.request.APIRequest;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.APIResponseCollection;
import com.buggycoder.domo.api.response.Advice;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.db.Prefs;
import com.buggycoder.domo.events.SupporteeEvents;
import com.buggycoder.domo.lib.JsonManager;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.lib.RequestManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.j256.ormlite.dao.Dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by shirish on 5/9/13.
 */
public class SupporteeAPI {

    public static void newAdviceRequest(Config config, String orgURL, String orgCode, String adviceRequest) throws UnsupportedEncodingException {

        String url = config.getSupporteeApi(orgURL) + "?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET);
        Logger.d(url);

        ObjectNode reqBody = JsonManager.getMapper().createObjectNode();
        reqBody.put("adviceRequest", adviceRequest);

        final String subscriberId = Prefs.get(config.getContext(), Prefs.Keys.PUSH_SUBSCRIBER_ID, "");
        if(!subscriberId.isEmpty()) {
            reqBody.put("subscriberId", adviceRequest);
        }

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<AdviceRequest>>(AdviceRequest.class, false) {
            @Override
            public void onResponse(APIResponse<AdviceRequest> response) {
                if (response.hasError) {
                    PubSub.publish(new SupporteeEvents.GetAdviceResult(response));
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
                    PubSub.publish(new SupporteeEvents.GetAdviceResult(response));

                    Logger.d(status.isCreated() + " | " + status.isUpdated());
                    Logger.dump(ar);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        responseHandler.setPath("response.advicerequest");

        APIRequest apiRequest = new APIRequest<APIResponse<AdviceRequest>>(
                Request.Method.POST,
                url,
                reqBody,
                responseHandler,
                new APIRequest.ErrorHandler() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.dump(volleyError);
                    }
                }
        );

        RequestManager.getRequestQueue().add(apiRequest);
    }

    ;

    public static void fetchAdviceRequest(Config config, String orgURL, String orgCode, String advicerequestId, String token) throws UnsupportedEncodingException {

        String url = config.getSupporteeApi(orgURL) + "/" + advicerequestId
                + "?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET)
                + "&token=" + URLEncoder.encode(token, APIRequest.PROTOCOL_CHARSET);

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<AdviceRequest>>(AdviceRequest.class, false) {
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

                    Logger.dump(ar);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        responseHandler.setPath("response.advicerequest");

        APIRequest apiRequest = new APIRequest<APIResponse<AdviceRequest>>(
                Request.Method.GET,
                url,
                null,
                responseHandler,
                new APIRequest.ErrorHandler() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        );

        RequestManager.getRequestQueue().add(apiRequest);
    }

    public static void markAdviceAttr(Config config, String orgURL, String orgCode, String advicerequestId, String adviceId, String token, Action action, int score) throws UnsupportedEncodingException {

        String reqBodyAttr, reqURLFrag;

        if (action == Action.HELPFUL) {
            reqBodyAttr = "helpful";
            reqURLFrag = "advicehelpful";
        } else if (action == Action.THANKYOU) {
            reqBodyAttr = "thankyou";
            reqURLFrag = "advicethankyou";
        } else {
            throw new IllegalArgumentException("Invalid action");
        }

        ObjectNode reqBody = JsonManager.getMapper().createObjectNode();
        reqBody.put(reqBodyAttr, score);

        String url = config.getSupporteeApi(orgURL) + "/" + advicerequestId + "/advice/" + adviceId + "/" + reqURLFrag
                + "?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET)
                + "&token=" + URLEncoder.encode(token, APIRequest.PROTOCOL_CHARSET);

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<AdviceRequest>>(AdviceRequest.class, false) {
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
                        status = daoAdv.createOrUpdate(a);
                        Logger.d(status.isCreated() + " | " + status.isUpdated());
                    }

                    Logger.dump(ar);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        responseHandler.setPath("response.advicerequest");

        APIRequest apiRequest = new APIRequest<APIResponse<AdviceRequest>>(
                Request.Method.POST,
                url,
                reqBody,
                responseHandler,
                new APIRequest.ErrorHandler() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.dump(volleyError);
                    }
                }
        );

        RequestManager.getRequestQueue().add(apiRequest);
    }


    public static void fetchUpdates(Config config) {

        List<AdviceRequest> adviceRequestList = null;

        try {
            Dao<AdviceRequest, String> arDao = DatabaseHelper.getDaoManager()
                                                             .getDao(AdviceRequest.class);
            adviceRequestList = arDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(adviceRequestList == null || adviceRequestList.size() == 0) {
            // nothing to do
            return;
        }

        final String url = config.getApiRoot() + "/app/updates";

        final ObjectMapper objectMapper = JsonManager.getMapper();
        ArrayNode reqBody = objectMapper.createArrayNode();

        ObjectNode arData;
        for(AdviceRequest ar : adviceRequestList) {
            arData = objectMapper.createObjectNode();
            arData.put("advicerequestId", ar.get_id());
            arData.put("token", ar.getAccessToken());
            reqBody.add(arData);
        }

        Logger.d("Requesting updates");
        Logger.dump(reqBody);

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponseCollection<AdviceRequest>>(AdviceRequest.class, true) {
            @Override
            public void onResponse(APIResponseCollection<AdviceRequest> response) {
                Logger.d("Received updates");

                if (response.hasError) {
                    Logger.d("Error: " + response.errors.toString());
                    return;
                }

                Dao<AdviceRequest, String> daoAr = null;
                Dao<Advice, String> daoAdv = null;

                try {
                    daoAr = DatabaseHelper
                                    .getDaoManager()
                                    .getDao(AdviceRequest.class);

                    daoAdv = DatabaseHelper
                                    .getDaoManager()
                                    .getDao(Advice.class);
                } catch (SQLException e) {
                    Logger.e(e);
                }

                if(daoAr == null || daoAdv == null) {
                    return;
                }

                List<AdviceRequest> arList = response.getResponse();
                Logger.d("arList.size: " + arList.size());

                Collection<Advice> adviceList;
                AdviceRequest tmpAr;
                Advice tmpAd;

                boolean hasNewData = false;

                try {
                    for(AdviceRequest ar : arList) {
                        tmpAr = daoAr.queryForId(ar.get_id());
                        if (tmpAr == null) {
                            daoAr.create(ar);
                            if(!hasNewData) {
                                hasNewData = true;
                            }
                        }

                        adviceList = ar.getResponses();
                        for (Advice a : adviceList) {
                            tmpAd = daoAdv.queryForId(a.get_id());
                            if (tmpAd == null) {
                                a.setAdviceRequest(ar);
                                daoAdv.create(a);
                                if(!hasNewData) {
                                    hasNewData = true;
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    Logger.e(e);
                } finally {
                    if(hasNewData) {
                        Logger.d("New updates received");
                        PubSub.publish(new SupporteeEvents.AdviceRequestsUpdated());
                    }
                }
            }
        };
        responseHandler.setPath("response.advicerequests");

        APIRequest apiRequest = new APIRequest<APIResponse<AdviceRequest>>(
                Request.Method.POST,
                url,
                reqBody,
                responseHandler,
                new APIRequest.ErrorHandler() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.dump(volleyError);
                    }
                }
        );

        RequestManager.getRequestQueue().add(apiRequest);
    }


    public static enum Action {
        HELPFUL,
        THANKYOU
    }

}
