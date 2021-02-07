package com.ltt.Model.Triple;

public class TripleID {
    private Integer sub;
    private Integer pre;
    private Integer obj;

    public TripleID(Integer sub, Integer pre, Integer obj) {
        this.sub = sub;
        this.pre = pre;
        this.obj = obj;
    }

    public Integer getSub() {
        return sub;
    }

    public void setSub(Integer sub) {
        this.sub = sub;
    }

    public Integer getPre() {
        return pre;
    }

    public void setPre(Integer pre) {
        this.pre = pre;
    }

    public Integer getObj() {
        return obj;
    }

    public void setObj(Integer obj) {
        this.obj = obj;
    }
}
