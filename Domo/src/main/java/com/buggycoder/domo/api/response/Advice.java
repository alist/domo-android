package com.buggycoder.domo.api.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "_id",
        "adviceGiver",
        "adviceResponse",
        "helpful",
        "thankyou",
        "modifiedDate",
        "status"
})
@DatabaseTable(tableName = "advice")
public class Advice {

    @JsonProperty("_id")
    @DatabaseField(id = true)
    private String _id;

    @JsonProperty("adviceGiver")
    @DatabaseField(canBeNull = false)
    private String adviceGiver;

    @JsonProperty("adviceResponse")
    @DatabaseField(canBeNull = false)
    private String adviceResponse;

    @JsonProperty("helpful")
    @DatabaseField(canBeNull = true)
    private Integer helpful;

    @JsonProperty("thankyou")
    @DatabaseField(canBeNull = true)
    private Integer thankyou;

    @JsonProperty("modifiedDate")
    @DatabaseField(canBeNull = true)
    private String modifiedDate;

    @JsonProperty("status")
    @DatabaseField(canBeNull = true)
    private String status;

    @JsonIgnore
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "adviceRequestId")
    private AdviceRequest adviceRequest;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("adviceGiver")
    public String getAdviceGiver() {
        return adviceGiver;
    }

    @JsonProperty("adviceGiver")
    public void setAdviceGiver(String adviceGiver) {
        this.adviceGiver = adviceGiver;
    }

    @JsonProperty("adviceResponse")
    public String getAdviceResponse() {
        return adviceResponse;
    }

    @JsonProperty("adviceResponse")
    public void setAdviceResponse(String adviceResponse) {
        this.adviceResponse = adviceResponse;
    }

    @JsonProperty("helpful")
    public Integer getHelpful() {
        return helpful;
    }

    @JsonProperty("helpful")
    public void setHelpful(Integer helpful) {
        this.helpful = helpful;
    }

    @JsonProperty("thankyou")
    public Integer getThankyou() {
        return thankyou;
    }

    @JsonProperty("thankyou")
    public void setThankyou(Integer thankyou) {
        this.thankyou = thankyou;
    }

    @JsonProperty("modifiedDate")
    public String getModifiedDate() {
        return modifiedDate;
    }

    @JsonProperty("modifiedDate")
    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    public AdviceRequest getAdviceRequest() {
        return adviceRequest;
    }

    public void setAdviceRequest(AdviceRequest adviceRequest) {
        this.adviceRequest = adviceRequest;
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