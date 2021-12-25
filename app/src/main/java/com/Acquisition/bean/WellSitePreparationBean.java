package com.Acquisition.bean;


/**
 * 井场准备
 */
public class WellSitePreparationBean {
    //水源情况
    private String headwaters;
    //通电情况
    private String electrify;
    //井场平整条件
    private String well_pad_leveling;
    //环境保障措施
    private String environmental_protection_guarantee;
    //青苗补偿情况
    private String young_crops;

    public String getHeadwaters() {
        return headwaters;
    }

    public void setHeadwaters(String headwaters) {
        this.headwaters = headwaters;
    }

    public String getElectrify() {
        return electrify;
    }

    public void setElectrify(String electrify) {
        this.electrify = electrify;
    }

    public String getWell_pad_leveling() {
        return well_pad_leveling;
    }

    public void setWell_pad_leveling(String well_pad_leveling) {
        this.well_pad_leveling = well_pad_leveling;
    }

    public String getEnvironmental_protection_guarantee() {
        return environmental_protection_guarantee;
    }

    public void setEnvironmental_protection_guarantee(String environmental_protection_guarantee) {
        this.environmental_protection_guarantee = environmental_protection_guarantee;
    }

    public String getYoung_crops() {
        return young_crops;
    }

    public void setYoung_crops(String young_crops) {
        this.young_crops = young_crops;
    }
}
