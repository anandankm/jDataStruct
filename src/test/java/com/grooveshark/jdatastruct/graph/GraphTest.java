package com.grooveshark.jdatastruct.graph;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;

public class GraphTest 
{

    public static final Logger log = Logger.getLogger(GraphTest.class);
    @Test
    public void loadGraph() {
        Graph graph = new Graph();
        String n1Val = "hong kong";
        String n2Val = "thailand";
        Node<String> n1 = new Node<String>(n1Val);
        Node<String> n2 = new Node<String>(n2Val);
        graph.addNode(n1);
        graph.addNode(n2);
        graph.addEdge(new Edge(n1, n2, 3));
        System.out.println(graph.toString());
    }
}
