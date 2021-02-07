package com.ltt.Model;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class DataStatistics {


    private String maxVocabulary;
    private String literalLength;

    private int tripleCount;
    private int entityCount;
    private int literalCount;
    private int typedSubjectCount;
    private int labelledSubjectCount;
    private int sameAsCount;

    private List<JSONObject> inDegree;
    private List<JSONObject> outDegree;



    public DataStatistics() {
    }

    public DataStatistics(int tripleCount, int entityCount, int literalCount, int typedSubjectCount, int labelledSubjectCount, int sameAsCount, String maxVocabulary, String literalLength, List<JSONObject> inDegree, List<JSONObject> outDegree) {
        this.tripleCount = tripleCount;
        this.entityCount = entityCount;
        this.literalCount = literalCount;
        this.typedSubjectCount = typedSubjectCount;
        this.labelledSubjectCount = labelledSubjectCount;
        this.sameAsCount = sameAsCount;
        this.maxVocabulary = maxVocabulary;
        this.literalLength = literalLength;
        this.inDegree = inDegree;
        this.outDegree = outDegree;
    }

    //    public DataStatistics(int tripleCount, int entityCount, int literalCount, int typedSubjectCount, int labelledSubjectCount, int sameAsCount, List<JSONObject> inDegree, List<JSONObject> outDegree) {
//        this.tripleCount = tripleCount;
//        this.entityCount = entityCount;
//        this.literalCount = literalCount;
//        this.typedSubjectCount = typedSubjectCount;
//        this.labelledSubjectCount = labelledSubjectCount;
//        this.sameAsCount = sameAsCount;
//        this.inDegree = inDegree;
//        this.outDegree = outDegree;
//    }

    public int getTripleCount() {
        return tripleCount;
    }

    public void setTripleCount(int tripleCount) {
        this.tripleCount = tripleCount;
    }

    public int getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(int entityCount) {
        this.entityCount = entityCount;
    }

    public int getLiteralCount() {
        return literalCount;
    }

    public void setLiteralCount(int literalCount) {
        this.literalCount = literalCount;
    }

    public int getTypedSubjectCount() {
        return typedSubjectCount;
    }

    public void setTypedSubjectCount(int typedSubjectCount) {
        this.typedSubjectCount = typedSubjectCount;
    }

    public int getLabelledSubjectCount() {
        return labelledSubjectCount;
    }

    public void setLabelledSubjectCount(int labelledSubjectCount) {
        this.labelledSubjectCount = labelledSubjectCount;
    }

    public int getSameAsCount() {
        return sameAsCount;
    }

    public void setSameAsCount(int sameAsCount) {
        this.sameAsCount = sameAsCount;
    }

    public List<JSONObject> getInDegree() {
        return inDegree;
    }

    public void setInDegree(List<JSONObject> inDegree) {
        this.inDegree = inDegree;
    }

    public List<JSONObject> getOutDegree() {
        return outDegree;
    }

    public void setOutDegree(List<JSONObject> outDegree) {
        this.outDegree = outDegree;
    }

    public String getMaxVocabulary() {
        return maxVocabulary;
    }

    public void setMaxVocabulary(String maxVocabulary) {
        this.maxVocabulary = maxVocabulary;
    }

    public String getLiteralLength() {
        return literalLength;
    }

    public void setLiteralLength(String literalLength) {
        this.literalLength = literalLength;
    }
}
