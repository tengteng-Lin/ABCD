package com.ltt.Model.Triple;

public class SnippetTriple implements Comparable<SnippetTriple>{
    public int s_id, p_id, o_id;
    public String subject, predicate, object,predicate_uri;
    public double weight = 0; //用于优先队列排序的权重
    public double kweight = 0, pweight = 0, cweight = 0, oweight = 0, iweight = 0;//分别对应五个指标的权重
    public double kwpweight = 0;//keyword pair对应的权重

    public int s_type,p_type,o_type;

    public SnippetTriple(String subject, String predicate, String object)
    {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public SnippetTriple(String subject, String predicate, String object, String predicate_uri) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;

        this.predicate_uri = predicate_uri;
    }

    public SnippetTriple(String subject, String predicate, String object, String predicate_uri, int s_type, int p_type, int o_type) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.predicate_uri = predicate_uri;
        this.s_type = s_type;
        this.p_type = p_type;
        this.o_type = o_type;
    }

    public SnippetTriple() {
    }

    @Override
    public int compareTo(SnippetTriple object) {
        if (this.weight > (object.weight))//这样写是降序，Collections.sort()默认升序
        {
            return -1;
        } else if (this.weight == object.weight)
        {
            return 0;
        } else
        {
            return 1;
        }
    }

    public void setW() {//权重为四部分的组合
        weight = 2 * kweight + pweight + cweight + iweight + oweight;
//        weight = 2 * kweight + kwpweight + pweight + cweight + iweight + oweight;
    }

    public double getW() {
        return weight;
    }

    public TripleName toTripleName() { return new TripleName(subject, predicate, object,predicate_uri,s_type,p_type,o_type); }
}
