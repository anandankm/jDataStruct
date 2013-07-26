package com.grooveshark.jdatastruct.graph.sample;

public class GEdge
{
    public int sUserid;
    public int eUserid;
    public GEdge(int sUserid, int eUserid) {
        this.sUserid = sUserid;
        this.eUserid = eUserid;
    }

    public String toString() {
        return "(" + this.sUserid + ", " + this.eUserid + ")";
    }
}
