package com.buggycoder.domo.events;

import com.buggycoder.domo.api.response.APIResponse;
import com.buggycoder.domo.api.response.AdviceRequest;

/**
 * Created by shirish on 9/9/13.
 */
public class SupporteeEvents {

    public static class GetAdviceResult {
        public APIResponse<AdviceRequest> result;

        public GetAdviceResult(APIResponse<AdviceRequest> r) {
            this.result = r;
        }
    }
}
