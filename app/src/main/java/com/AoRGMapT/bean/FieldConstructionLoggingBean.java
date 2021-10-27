package com.AoRGMapT.bean;


public class FieldConstructionLoggingBean {

    //层位
    private String horizon;
    //顶届深度
    private String top_boundary_depth;
    //底届深度
    private String bottom_boundary_depth;
    //厚度
    private String thickness;
    //解释结论
    private String interpretation_conclusion;

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

    public String getInterpretation_conclusion() {
        return interpretation_conclusion;
    }

    public void setInterpretation_conclusion(String interpretation_conclusion) {
        this.interpretation_conclusion = interpretation_conclusion;
    }
}
