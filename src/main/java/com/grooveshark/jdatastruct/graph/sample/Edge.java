package com.grooveshark.jdatastruct.graph.sample;

public class Edge
{
    public int sUserid;
    public int eUserid;
    public Edge(int sUserid, int eUserid) {
        this.sUserid = sUserid;
        this.eUserid = eUserid;
    }

    public String toString() {
        return "(" + this.sUserid + ", " + this.eUserid + ")";
    }
}
