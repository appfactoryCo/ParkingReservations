package com.ridecell.app.ridecell.DataModels;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class SpotData implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cost_per_minute")
    @Expose
    private String costPerMinute;
    @SerializedName("max_reserve_time_mins")
    @Expose
    private Integer maxReserveTimeMins;
    @SerializedName("min_reserve_time_mins")
    @Expose
    private Integer minReserveTimeMins;
    @SerializedName("is_reserved")
    @Expose
    private Boolean isReserved;
    @SerializedName("reserved_until")
    @Expose
    private Object reservedUntil;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     * The lng
     */
    public String getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     * The lng
     */
    public void setLng(String lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The costPerMinute
     */
    public String getCostPerMinute() {
        return costPerMinute;
    }

    /**
     *
     * @param costPerMinute
     * The cost_per_minute
     */
    public void setCostPerMinute(String costPerMinute) {
        this.costPerMinute = costPerMinute;
    }

    /**
     *
     * @return
     * The maxReserveTimeMins
     */
    public Integer getMaxReserveTimeMins() {
        return maxReserveTimeMins;
    }

    /**
     *
     * @param maxReserveTimeMins
     * The max_reserve_time_mins
     */
    public void setMaxReserveTimeMins(Integer maxReserveTimeMins) {
        this.maxReserveTimeMins = maxReserveTimeMins;
    }

    /**
     *
     * @return
     * The minReserveTimeMins
     */
    public Integer getMinReserveTimeMins() {
        return minReserveTimeMins;
    }

    /**
     *
     * @param minReserveTimeMins
     * The min_reserve_time_mins
     */
    public void setMinReserveTimeMins(Integer minReserveTimeMins) {
        this.minReserveTimeMins = minReserveTimeMins;
    }

    /**
     *
     * @return
     * The isReserved
     */
    public Boolean getIsReserved() {
        return isReserved;
    }

    /**
     *
     * @param isReserved
     * The is_reserved
     */
    public void setIsReserved(Boolean isReserved) {
        this.isReserved = isReserved;
    }

    /**
     *
     * @return
     * The reservedUntil
     */
    public Object getReservedUntil() {
        return reservedUntil;
    }

    /**
     *
     * @param reservedUntil
     * The reserved_until
     */
    public void setReservedUntil(Object reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

}