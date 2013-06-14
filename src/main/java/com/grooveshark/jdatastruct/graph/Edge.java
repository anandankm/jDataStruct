package com.grooveshark.jdatastruct.graph;


import java.util.HashSet;

import org.apache.log4j.Logger;

public class Edge
{
    public static final Logger log = Logger.getLogger(Edge.class);

    protected Node a, b;

    protected double weight = 0d;

    public Edge() {
    }

    public Edge(Node a, Node b) {
        this.a= a;
        this.b= b;
    }

    public Edge(Node a, Node b, double weight) {
        this.a= a;
        this.b= b;
        this.weight = weight;
    }

}
