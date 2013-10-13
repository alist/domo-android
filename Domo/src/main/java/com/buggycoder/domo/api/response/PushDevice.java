package com.buggycoder.domo.api.response;

/**
 * Created by shirish on 13/10/13.
 */

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "deviceId",
        "deviceType",
        "deviceToken",
        "_id",
        "subscriberId"
})
public class PushDevice {

    @JsonProperty("deviceId")
    private String deviceId;
    @JsonProperty("deviceType")
    private String deviceType;
    @JsonProperty("deviceToken")
    private String deviceToken;
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("subscriberId")
    private String subscriberId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("deviceId")
    public String getDeviceId() {
        return deviceId;
    }

    @JsonProperty("deviceId")
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @JsonProperty("deviceType")
    public String getDeviceType() {
        return deviceType;
    }

    @JsonProperty("deviceType")
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @JsonProperty("deviceToken")
    public String getDeviceToken() {
        return deviceToken;
    }

    @JsonProperty("deviceToken")
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("subscriberId")
    public String getSubscriberId() {
        return subscriberId;
    }

    @JsonProperty("subscriberId")
    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}