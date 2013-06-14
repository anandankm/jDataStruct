package com.grooveshark.jdatastruct.graph;


import java.util.Vector;

import org.apache.log4j.Logger;

public class Graph
{

    public static final Logger log = Logger.getLogger(Graph.class);

    protected Vector<Node> vertices;
    protected Vector<Edge> edges;

    public Graph() {
        this.vertices = new Vector<Node>();
        this.edges = new Vector<Edge>();
    }

    public boolean addNode(Node n) {
        this.vertices.add(n);
        return true;
    }

    public boolean addEdge(Edge e) {
        this.edges.add(e);
        return true;
    }

    public String toString() {
        return "Graph<Vertices, Edges>: <" +
            this.vertices.toString() + ", " +
            this.edges.toString();
    }
}
