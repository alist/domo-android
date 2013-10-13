package com.buggycoder.domo.api;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.request.APIRequest;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.AdviceRequest;
import com.buggycoder.domo.api.response.PushDevice;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.lib.JsonManager;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.RequestManager;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by shirish on 13/10/13.
 */
public class PushAPI {

    public static final String DEVICE_TYPE = "android";

    public static void register(final Config config, final String registrationId, final String orgURL, final String orgCode) throws UnsupportedEncodingException {
        String registerUrl = config.getPushApi() + "/register"
                + "?token=" + URLEncoder.encode(orgURL + "|" + orgCode, APIRequest.PROTOCOL_CHARSET);

        ObjectNode reqBody = JsonManager.getMapper().createObjectNode();
        reqBody.put("deviceToken", registrationId);
        reqBody.put("deviceType", DEVICE_TYPE);

        ObjectNode deviceMeta = JsonManager.getMapper().createObjectNode();
        deviceMeta.put("type", "android");

        reqBody.put("deviceMeta", deviceMeta);
        Logger.d(reqBody.toString());

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<PushDevice>>(PushDevice.class, false) {
            @Override
            public void onResponse(APIResponse<PushDevice> response) {
                if (response.hasError) {
                    Logger.d("Error: " + response.errors.toString());
                    return;
                }

                PushDevice pd = response.getResponse();
                Logger.dump(pd);
                Logger.d("subscriberId: " + pd.getSubscriberId());
            }
        };

        responseHandler.setPath("response");

        APIRequest apiRequest = new APIRequest<APIResponse<AdviceRequest>>(
                Request.Method.POST,
                registerUrl,
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
}
