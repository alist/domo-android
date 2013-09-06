package com.buggycoder.domo.api.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "_id",
        "accessToken",
        "accessURL",
        "adviceRequest",
        "organization",
        "responses",
        "createdOn"
})
@DatabaseTable(tableName = "advicerequests")
public class AdviceRequest {

    @JsonProperty("_id")
    @DatabaseField(id = true)
    private String _id;

    @JsonProperty("accessToken")
    @DatabaseField(canBeNull = false)
    private String accessToken;

    @JsonProperty("accessURL")
    @DatabaseField(canBeNull = false)
    private String accessURL;

    @JsonProperty("adviceRequest")
    @DatabaseField(canBeNull = false)
    private String adviceRequest;

    @JsonProperty("organization")
    @DatabaseField(canBeNull = false)
    private String organization;

    @JsonProperty("createdOn")
    @DatabaseField(canBeNull = false)
    private String createdOn;

    @JsonProperty("responses")
    @ForeignCollectionField(eager = true)
    private Collection<Advice> responses = new ArrayList<Advice>();

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("accessToken")
    public String getAccessToken() {
        return accessToken;
    }

    @JsonProperty("accessToken")
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonProperty("accessURL")
    public String getAccessURL() {
        return accessURL;
    }

    @JsonProperty("accessURL")
    public void setAccessURL(String accessURL) {
        this.accessURL = accessURL;
    }

    @JsonProperty("adviceRequest")
    public String getAdviceRequest() {
        return adviceRequest;
    }

    @JsonProperty("adviceRequest")
    public void setAdviceRequest(String adviceRequest) {
        this.adviceRequest = adviceRequest;
    }

    @JsonProperty("organization")
    public String getOrganization() {
        return organization;
    }

    @JsonProperty("organization")
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @JsonProperty("responses")
    public Collection<Advice> getResponses() {
        return responses;
    }

    @JsonProperty("responses")
    public void setResponses(Collection<Advice> responses) {
        this.responses = responses;
    }

    @JsonProperty("createdOn")
    public String getCreatedOn() {
        return createdOn;
    }

    @JsonProperty("createdOn")
    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
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
