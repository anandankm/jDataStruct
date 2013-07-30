package com.grooveshark.jdatastruct.graph.sample.entities;

public class GEdge
{
    public long sUserid;
    public long eUserid;
    public static final String EDGE_INDEX_KEY = "edge";
    public GEdge(long sUserid, long eUserid) {
        this.sUserid = sUserid;
        this.eUserid = eUserid;
    }

    public String toString() {
        return "(" + this.sUserid + ", " + this.eUserid + ")";
    }
}
