package com.ltt.Model;

import java.util.Set;

public class InstanceFeature {
    private Set<Integer> predicateID;
    private Set<Integer> classID;

    public InstanceFeature() {
    }

    public InstanceFeature(Set<Integer> predicateID, Set<Integer> classID) {
        this.predicateID = predicateID;
        this.classID = classID;
    }

    public Set<Integer> getPredicateID() {
        return predicateID;
    }

    public void setPredicateID(Set<Integer> predicateID) {
        this.predicateID = predicateID;
    }

    public Set<Integer> getClassID() {
        return classID;
    }

    public void setClassID(Set<Integer> classID) {
        this.classID = classID;
    }

    @Override
    public String toString() {
        return
                predicateID +
                "#" + classID
                ;
    }

    @Override
    public boolean equals(Object obj) {
        InstanceFeature two = (InstanceFeature)obj;
        Set<Integer> twoPre = two.getPredicateID();
        Set<Integer> twoCls = two.getClassID();


        return equals(twoPre,this.predicateID)&&equals(twoCls,this.classID);
    }

    @Override
    public int hashCode() {
        int result = predicateID.hashCode();

        //TODO   相加会出现问题吗？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？？
        return predicateID.hashCode()+classID.hashCode();
    }

    public static boolean equals(Set<Integer> set1, Set<Integer> set2){

        if(set1 == null || set2 ==null){
            return false;
        }

        if(set1.size()!=set2.size()){
            return false;
        }

        return set1.containsAll(set2);

    }
}
