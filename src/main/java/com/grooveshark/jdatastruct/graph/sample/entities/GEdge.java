package com.grooveshark.jdatastruct.graph.sample.entities;

public class GEdge
{
    public int sUserid;
    public int eUserid;
    public static final String EDGE_INDEX_KEY = "edge";
    public GEdge(int sUserid, int eUserid) {
        this.sUserid = sUserid;
        this.eUserid = eUserid;
    }

    public String toString() {
        return "(" + this.sUserid + ", " + this.eUserid + ")";
    }
}
