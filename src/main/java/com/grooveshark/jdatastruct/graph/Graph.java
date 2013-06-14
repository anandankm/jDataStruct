package com.grooveshark.jdatastruct.graph;


import java.util.Vector;

import org.apache.log4j.Logger;

public class Graph
{

    public static final Logger log = Logger.getLogger(Graph.class);

    protected Vector<Node> vertices = new Vector<Node>();
    protected Vector<Edge> edges = new Vector<Edge>();

    public Graph() {
    }
}
