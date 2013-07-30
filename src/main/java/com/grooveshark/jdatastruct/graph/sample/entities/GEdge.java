package com.grooveshark.jdatastruct.graph.sample.entities;

import java.util.Map;
import static org.neo4j.helpers.collection.MapUtil.map;

public class GEdge
{
    public long sUserid;
    public long eUserid;
    public static final String REL_INDEX = "followers";
    public static final String EDGE_INDEX_KEY = "edge";
    public static final Map<String, Object> REL_PROPS = map("name", "follows");

    public GEdge(long sUserid, long eUserid) {
        this.sUserid = sUserid;
        this.eUserid = eUserid;
    }

    public String toString() {
        return "(" + this.sUserid + ", " + this.eUserid + ")";
    }
}
