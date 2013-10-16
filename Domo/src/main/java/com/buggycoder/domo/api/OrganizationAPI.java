package com.buggycoder.domo.api;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.request.APIRequest;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.APIResponseCollection;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.api.response.Organization;
import com.buggycoder.domo.app.Config;
import com.buggycoder.domo.db.DatabaseHelper;
import com.buggycoder.domo.events.OrganizationEvents;
import com.buggycoder.domo.lib.Logger;
import com.buggycoder.domo.lib.PubSub;
import com.buggycoder.domo.lib.RequestManager;
import com.j256.ormlite.dao.Dao;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Created by shirish on 5/9/13.
 */
public class OrganizationAPI {

    public static void getOrganizations(Config config) {

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponseCollection<Organization>>(Organization.class, true) {
            @Override
            public void onResponse(APIResponseCollection<Organization> response) {
                if (response.hasError) {
                    PubSub.publish(new OrganizationEvents.GetOrganizationsResult(response));
                    Logger.d("Error: " + response.errors.toString());
                    return;
                }

                try {
                    Dao<Organization, String> daoOrg =
                            DatabaseHelper
                                    .getDaoManager()
                                    .getDao(Organization.class);

                    for (Organization o : response.getResponse()) {
                        Dao.CreateOrUpdateStatus status = daoOrg.createOrUpdate(o);
                        Logger.d(status.isCreated() + " | " + status.isUpdated());
                    }

                    PubSub.publish(new OrganizationEvents.GetOrganizationsResult(response));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        responseHandler.setPath("response.organizations");

        APIRequest apiRequest = new APIRequest<APIResponseCollection<Organization>>(
                Request.Method.GET,
                config.getOrganizationsApi(),
                null,
                responseHandler,
                new APIRequest.ErrorHandler() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Logger.dump(volleyError);
                    }
                }
        );

        RequestManager.getRequestQueue().add(apiRequest);
    }

    public static void getOrganization(Config config, String orgURL) {

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<Organization>>(Organization.class, false) {
            @Override
            public void onResponse(APIResponse<Organization> response) {
                if (response.hasError) {
                    Logger.d("Error: " + response.errors.toString());
                    return;
                }

                Organization organization = response.getResponse();
                Logger.dump(organization);

                try {
                    Dao<Organization, String> daoOrg =
                            DatabaseHelper
                                    .getDaoManager()
                                    .getDao(Organization.class);
                    Dao.CreateOrUpdateStatus status = daoOrg.createOrUpdate(organization);
                    Logger.d(status.isCreated() + " | " + status.isUpdated());

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        responseHandler.setPath("response.organization");

        APIRequest apiRequest = new APIRequest<APIResponse<Organization>>(
                Request.Method.GET,
                config.getOrganizationsApi() + orgURL,
                null,
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

    public static void checkCode(Config config, String orgURL, String orgCode) throws UnsupportedEncodingException {

        APIRequest.ResponseHandler responseHandler = new APIRequest.ResponseHandler<APIResponse<MyOrganization>>(MyOrganization.class, false) {
            @Override
            public void onResponse(APIResponse<MyOrganization> response) {
                if (response.hasError) {
                    Logger.d("Error: " + response.errors.toString());
                    PubSub.publish(new OrganizationEvents.CheckOrgCodeResult(response));
                    return;
                }

                MyOrganization organization = response.getResponse();
                Logger.dump(organization);

                try {
                    Dao<MyOrganization, String> daoOrg =
                            DatabaseHelper
                                    .getDaoManager()
                                    .getDao(MyOrganization.class);
                    Dao.CreateOrUpdateStatus status = daoOrg.createOrUpdate(organization);
                    Logger.d(status.isCreated() + " | " + status.isUpdated());

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                PubSub.publish(new OrganizationEvents.CheckOrgCodeResult(response));
            }
        };

        responseHandler.setPath("response.organization");

        APIRequest apiRequest = new APIRequest<APIResponse<MyOrganization>>(
                Request.Method.GET,
                config.getOrganizationsApi() + orgURL + "/codecheck?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET),
                null,
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