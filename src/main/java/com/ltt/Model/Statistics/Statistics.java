package com.ltt.Model.Statistics;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class Statistics {
    private List<JSONObject> classDis;
    private List<JSONObject> propertyDis;



    public Statistics() {
    }

    public List<JSONObject> getClassDis() {
        return classDis;
    }

    public void setClassDis(List<JSONObject> classDis) {
        this.classDis = classDis;
    }

    public List<JSONObject> getPropertyDis() {
        return propertyDis;
    }

    public void setPropertyDis(List<JSONObject> propertyDis) {
        this.propertyDis = propertyDis;
    }


}
