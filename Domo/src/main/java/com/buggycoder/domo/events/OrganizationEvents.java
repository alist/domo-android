package com.buggycoder.domo.events;


import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.APIResponseCollection;
import com.buggycoder.domo.api.response.MyOrganization;
import com.buggycoder.domo.api.response.Organization;

/**
 * Created by shirish on 8/9/13.
 */
public class OrganizationEvents {

    public static class MyOrganizationsUpdate {

    }

    public static class GetOrganizations {

    }

    public static class GetOrganizationsResult {
        public APIResponseCollection<Organization> result;

        public GetOrganizationsResult(APIResponseCollection<Organization> r) {
            this.result = r;
        }
    }

    public static class CheckOrgCodeResult {
        public APIResponse<MyOrganization> result;

        public CheckOrgCodeResult(APIResponse<MyOrganization> r) {
            this.result = r;
        }
    }
}
