package com.grooveshark.jdatastruct.graph;


import java.util.Vector;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class Graph<T extends Comparable>
{

    public static final Logger log = Logger.getLogger(Graph.class);

    protected TreeMap<T, Node> vertices;
    protected Vector<Edge> edges;

    protected boolean directed = false;

    public Graph() {
        this.vertices = new TreeMap<T, Node>();
        this.edges = new Vector<Edge>();
    }

    public Node addNode(T v) {
        if (this.vertices.containsKey(v)) {
            return this.vertices.get(v);
        }
        Node n = new Node(v);
        this.vertices.put(v, n);
        return n;
    }

    public boolean addEdge(Edge e) {
        this.edges.add(e);
        e.addBToA();
        if (!this.directed) {
            e.addAToB();
        }
        return true;
    }

    public String toString() {
        return "Graph<Vertices, Edges>: <" +
            this.vertices.toString() + ", " +
            this.edges.toString();
    }

    /**
     * Setter method for directed
     */
    public void setDirected(boolean directed) {
        this.directed = directed;
    }
    /**
     * Getter method for directed
     */
    public boolean getDirected() {
        return this.directed;
    }
}
