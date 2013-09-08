package com.buggycoder.domo.api.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.APIResponseCollection;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.api.response.Organization;
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

    static final String API_ROOT = "http://buggycoder.com:4000/api/v1/organizations/";


    public static void getOrganizations() {

        APIRequest apiRequest = new APIRequest<APIResponseCollection<Organization>>(
                Request.Method.GET,
                API_ROOT,
                null,
                Organization.class,
                true,
                new Response.Listener<APIResponseCollection<Organization>>() {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.d(volleyError.toString());
                    }
                }
        );

        apiRequest.setPath("organizations");

        RequestManager.getRequestQueue().add(apiRequest);
    }


    public static void getOrganization(String orgURL) {

        APIRequest apiRequest = new APIRequest<APIResponse<Organization>>(
                Request.Method.GET,
                API_ROOT + orgURL,
                null,
                Organization.class,
                new Response.Listener<APIResponse<Organization>>() {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.d(volleyError.toString());
                    }
                }
        );

        apiRequest.setPath("organization");
        RequestManager.getRequestQueue().add(apiRequest);
    }

    public static void checkCode(String orgURL, String orgCode) throws UnsupportedEncodingException {

        APIRequest apiRequest = new APIRequest<APIResponse<MyOrganization>>(
                Request.Method.GET,
                API_ROOT + orgURL + "/codecheck?code=" + URLEncoder.encode(orgCode, APIRequest.PROTOCOL_CHARSET),
                null,
                Organization.class,
                new Response.Listener<APIResponse<MyOrganization>>() {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Logger.d(volleyError.toString());
                    }
                }
        );

        apiRequest.setPath("organization");
        RequestManager.getRequestQueue().add(apiRequest);
    }
}
