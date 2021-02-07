package com.ltt.Model;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class SchemaStatistics {


    private int propertyUsageCountPerSubject;
    private int propertyUsageCountPerObjetc;
    private int clssDefined;

    private List<JSONObject> classDis;
    private List<JSONObject> propertyDis;
    private int classUsage;
    private int propertyUsage;



    public SchemaStatistics() {
    }

    public SchemaStatistics(List<JSONObject> classDis, List<JSONObject> propertyDis, int classUsage, int propertyUsage, int propertyUsageCountPerSubject, int propertyUsageCountPerObjetc, int clssDefined) {
        this.classDis = classDis;
        this.propertyDis = propertyDis;
        this.classUsage = classUsage;
        this.propertyUsage = propertyUsage;
        this.propertyUsageCountPerSubject = propertyUsageCountPerSubject;
        this.propertyUsageCountPerObjetc = propertyUsageCountPerObjetc;
        this.clssDefined = clssDefined;

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

    public int getClassUsage() {
        return classUsage;
    }

    public void setClassUsage(int classUsage) {
        this.classUsage = classUsage;
    }

    public int getPropertyUsage() {
        return propertyUsage;
    }

    public void setPropertyUsage(int propertyUsage) {
        this.propertyUsage = propertyUsage;
    }

    public int getPropertyUsageCountPerSubject() {
        return propertyUsageCountPerSubject;
    }

    public void setPropertyUsageCountPerSubject(int propertyUsageCountPerSubject) {
        this.propertyUsageCountPerSubject = propertyUsageCountPerSubject;
    }

    public int getPropertyUsageCountPerObjetc() {
        return propertyUsageCountPerObjetc;
    }

    public void setPropertyUsageCountPerObjetc(int propertyUsageCountPerObjetc) {
        this.propertyUsageCountPerObjetc = propertyUsageCountPerObjetc;
    }

    public int getClssDefined() {
        return clssDefined;
    }

    public void setClssDefined(int clssDefined) {
        this.clssDefined = clssDefined;
    }

}
