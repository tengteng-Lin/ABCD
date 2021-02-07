package com.ltt.Model.Triple;

public class TripleName
{
    public String sub;//都是label
    public String pre;
    public String obj;

    public String pre_uri;

    public int sub_type; //0:entity    1:other
    public int pre_type; //0:type  1:other
    public int obj_type;//0:  class         1:literal



    public TripleName(String sub, String pre, String obj)
    {
        this.sub = sub;
        this.pre = pre;
        this.obj = obj;
    }

    public TripleName(String sub, String pre, String obj, String pre_uri) {
        this.sub = sub;
        this.pre = pre;
        this.obj = obj;
        this.pre_uri = pre_uri;
    }

    public TripleName(String sub, String pre, String obj, String pre_uri, int sub_type, int pre_type, int obj_type) {
        this.sub = sub;
        this.pre = pre;
        this.obj = obj;
        this.pre_uri = pre_uri;
        this.sub_type = sub_type;
        this.pre_type = pre_type;
        this.obj_type = obj_type;
    }

    public TripleName()
    {
    }

    public String getSub()
    {
        return sub;
    }

    public void setSub(String sub)
    {
        this.sub = sub;
    }

    public String getPre()
    {
        return pre;
    }

    public void setPre(String pre)
    {
        this.pre = pre;
    }

    public String getObj()
    {
        return obj;
    }

    public void setObj(String obj)
    {
        this.obj = obj;
    }

    public String getPre_uri() {
        return pre_uri;
    }

    public void setPre_uri(String pre_uri) {
        this.pre_uri = pre_uri;
    }

    public int getSub_type() {
        return sub_type;
    }

    public void setSub_type(int sub_type) {
        this.sub_type = sub_type;
    }

    public int getPre_type() {
        return pre_type;
    }

    public void setPre_type(int pre_type) {
        this.pre_type = pre_type;
    }

    public int getObj_type() {
        return obj_type;
    }

    public void setObj_type(int obj_type) {
        this.obj_type = obj_type;
    }
}
