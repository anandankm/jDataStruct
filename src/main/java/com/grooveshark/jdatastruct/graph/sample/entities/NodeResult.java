package com.grooveshark.jdatastruct.graph.sample.entities;


import com.grooveshark.util.MathUtils;

public class NodeResult implements Comparable<NodeResult>
{
    public Float weight;
    public Double distanceFromBase;
    public GNode node;

    public NodeResult(Float weight, GNode node)
    {
        this.weight = weight;
        this.node = node;
    }

    public void setDistanceFromBase(GNode baseNode)
    {
        Float lat1 = this.node.location.latitude;
        Float long1 = this.node.location.longitude;
        Float lat2 = baseNode.location.latitude;
        Float long2 = baseNode.location.longitude;
        this.distanceFromBase = MathUtils.sphericalDistance(lat1, long1, lat2, long2);
    }

    @Override
    public int compareTo(NodeResult o)
    {
        if (this.weight.equals(o.weight)) {
            if (this.distanceFromBase.equals(o.distanceFromBase)) {
                return this.node.userid.compareTo(o.node.userid);
            } else {
                return this.distanceFromBase.compareTo(o.distanceFromBase);
            }
        } else {
            return this.weight.compareTo(o.weight);
        }
    }

    public boolean equals(NodeResult o)
    {
        if (this.weight.equals(o.weight)) {
            if (this.distanceFromBase.equals(o.distanceFromBase)) {
                return this.node.userid.equals(o.node.userid);
            } else {
                return this.distanceFromBase.equals(o.distanceFromBase);
            }
        } else {
            return this.weight.equals(o.weight);
        }
    }

}
