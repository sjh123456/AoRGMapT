package com.AoRGMapT.bean;

import android.widget.EditText;

public class CoreDescriptionBean {

    //样本类型
    private String sample_type;
    //层位
    private String horizon;
    //顶界深度
    private String top_boundary_depth;
    //底界深度
    private String bottom_boundary_depth;
    //进尺
    private String footage;
    //心长
    private String long_heart;
    //收获率
    private String harvest_rate;
    //岩心描述
    private String core_description;

    public String getSample_type() {
        return sample_type;
    }

    public void setSample_type(String sample_type) {
        this.sample_type = sample_type;
    }

    public String getHorizon() {
        return horizon;
    }

    public void setHorizon(String horizon) {
        this.horizon = horizon;
    }

    public String getTop_boundary_depth() {
        return top_boundary_depth;
    }

    public void setTop_boundary_depth(String top_boundary_depth) {
        this.top_boundary_depth = top_boundary_depth;
    }

    public String getBottom_boundary_depth() {
        return bottom_boundary_depth;
    }

    public void setBottom_boundary_depth(String bottom_boundary_depth) {
        this.bottom_boundary_depth = bottom_boundary_depth;
    }

    public String getFootage() {
        return footage;
    }

    public void setFootage(String footage) {
        this.footage = footage;
    }

    public String getLong_heart() {
        return long_heart;
    }

    public void setLong_heart(String long_heart) {
        this.long_heart = long_heart;
    }

    public String getHarvest_rate() {
        return harvest_rate;
    }

    public void setHarvest_rate(String harvest_rate) {
        this.harvest_rate = harvest_rate;
    }

    public String getCore_description() {
        return core_description;
    }

    public void setCore_description(String core_description) {
        this.core_description = core_description;
    }
}
