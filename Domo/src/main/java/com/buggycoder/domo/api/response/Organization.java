package com.buggycoder.domo.api.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
        "id",
        "bannerURL",
        "code",
        "orgURL",
        "displayName",
        "region",
        "city"
})
@DatabaseTable(tableName = "organizations")
public class Organization {

    @JsonProperty("_id")
    @DatabaseField(id = true)
    private String _id;

    @JsonProperty("id")
    @DatabaseField(canBeNull = false)
    private String id;

    @JsonProperty("orgURL")
    @DatabaseField(canBeNull = false)
    private String orgURL;

    @JsonProperty("displayName")
    @DatabaseField(canBeNull = false)
    private String displayName;


    @JsonProperty("code")
    @DatabaseField(canBeNull = true)
    private String code;

    @JsonProperty("bannerURL")
    @DatabaseField(canBeNull = true)
    private String bannerURL;

    @JsonProperty("region")
    @DatabaseField(canBeNull = true)
    private String region;

    @JsonProperty("city")
    @DatabaseField(canBeNull = true)
    private String city;


    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    @JsonProperty("region")
    public void setRegion(String region) {
        this.region = region;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("bannerURL")
    public String getBannerURL() {
        return bannerURL;
    }

    @JsonProperty("bannerURL")
    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("orgURL")
    public String getOrgURL() {
        return orgURL;
    }

    @JsonProperty("orgURL")
    public void setOrgURL(String orgURL) {
        this.orgURL = orgURL;
    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
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