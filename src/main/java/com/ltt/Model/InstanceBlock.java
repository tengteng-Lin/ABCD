package com.ltt.Model;

public class InstanceBlock {
    private int idx;
    private InstanceFeature instanceFeature;
//    private Set<Integer> instanceID;

    public InstanceBlock() {
    }

    public InstanceBlock(int idx, InstanceFeature instanceFeature) {
        this.idx = idx;
        this.instanceFeature = instanceFeature;
//        this.instanceID = instanceID;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public InstanceFeature getInstanceFeature() {
        return instanceFeature;
    }

    public void setInstanceFeature(InstanceFeature instanceFeature) {
        this.instanceFeature = instanceFeature;
    }

//    public Set<Integer> getInstanceID() {
//        return instanceID;
//    }
//
//    public void setInstanceID(Set<Integer> instanceID) {
//        this.instanceID = instanceID;
//    }
}
