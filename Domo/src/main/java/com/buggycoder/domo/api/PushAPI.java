package com.buggycoder.domo.api;

import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.request.APIRequest;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.PushDevice;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.Prefs;
import com.buggycoder.domo.lib.JsonManager;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.RequestManager;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by shirish on 13/10/13.
 */
public class PushAPI {

    public static final String DEVICE_TYPE = "android";

    public static void register(final Config config, final String registrationId, final String orgURL, final String orgCode) throws UnsupportedEncodingException {
        String registerUrl = config.getPushApi() + "register"
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

                final SharedPreferences.Editor prefEditor = Prefs.getSharedPreferences(config.getContext()).edit();
                prefEditor.putBoolean(Prefs.Keys.PUSH_REG_COMPLETE, true);
                prefEditor.putString(Prefs.Keys.PUSH_SUBSCRIBER_ID, pd.getSubscriberId());
                prefEditor.putString(Prefs.Keys.PUSH_DEVICE_ID, pd.getDeviceId());
                prefEditor.putLong(Prefs.Keys.PUSH_REG_TS, new Date().getTime());
                prefEditor.commit();

                Logger.d("subscriberId: " + pd.getSubscriberId());
            }
        };

        responseHandler.setPath("response");

        APIRequest apiRequest = new APIRequest<APIResponse<PushDevice>>(
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


    public static void update(final Config config, final String registrationId) throws UnsupportedEncodingException {

        final SharedPreferences pref = Prefs.getSharedPreferences(config.getContext());
        final String prevRegId = pref.getString(Prefs.Keys.PUSH_REG_ID, "");
        final String subscriberId = pref.getString(Prefs.Keys.PUSH_SUBSCRIBER_ID, "");
        final String deviceId = pref.getString(Prefs.Keys.PUSH_DEVICE_ID, "");

        if(prevRegId.isEmpty() || subscriberId.isEmpty() || deviceId.isEmpty() || prevRegId.equals(registrationId)) {
            return;
        }

        String updateUrl = config.getPushApi() + "devicetoken";

        ObjectNode reqBody = JsonManager.getMapper().createObjectNode();
        reqBody.put("subscriberId", subscriberId);
        reqBody.put("deviceId", deviceId);
        reqBody.put("deviceToken", registrationId);

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

                final SharedPreferences.Editor prefEditor = pref.edit();
                prefEditor.putString(Prefs.Keys.PUSH_DEVICE_ID, pd.getDeviceId());
                prefEditor.putLong(Prefs.Keys.PUSH_REG_TS, new Date().getTime());
                prefEditor.commit();

                Logger.d("subscriberId: " + pd.getSubscriberId());
            }
        };

        responseHandler.setPath("response");

        APIRequest apiRequest = new APIRequest<APIResponse<PushDevice>>(
                Request.Method.POST,
                updateUrl,
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
