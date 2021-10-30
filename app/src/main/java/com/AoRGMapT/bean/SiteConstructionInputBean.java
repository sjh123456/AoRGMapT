package com.AoRGMapT.bean;


public class SiteConstructionInputBean {

    private String horizon;
    private String top_boundary_depth;
    private String bottom_boundary_depth;
    private String thickness;
    private String display_level;

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

    public String getThickness() {
        return thickness;
    }

    public void setThickness(String thickness) {
        this.thickness = thickness;
    }

    public String getDisplay_level() {
        return display_level;
    }

    public void setDisplay_level(String display_level) {
        this.display_level = display_level;
    }
}
