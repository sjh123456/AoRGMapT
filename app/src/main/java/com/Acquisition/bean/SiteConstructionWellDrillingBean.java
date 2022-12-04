package com.Acquisition.bean;


public class SiteConstructionWellDrillingBean {

    //施工累计天数
    private String construction_days;
    //当前井深
    private String well_depth;
    //日进尺
    private String daily_footage;
    //钻遇地层
    private String stratum;
    //施工单位
    private String construction_unit;

    public String getConstruction_days() {
        return construction_days;
    }

    public void setConstruction_days(String construction_days) {
        this.construction_days = construction_days;
    }

    public String getWell_depth() {
        return well_depth;
    }

    public void setWell_depth(String well_depth) {
        this.well_depth = well_depth;
    }

    public String getDaily_footage() {
        return daily_footage;
    }

    public void setDaily_footage(String daily_footage) {
        this.daily_footage = daily_footage;
    }

    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

    public String getConstruction_unit() {
        return construction_unit;
    }

    public void setConstruction_unit(String construction_unit) {
        this.construction_unit = construction_unit;
    }
}
