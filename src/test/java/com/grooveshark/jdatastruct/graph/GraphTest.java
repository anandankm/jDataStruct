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

    public Graph<String> graph = new Graph<String>();
    public static final Logger log = Logger.getLogger(GraphTest.class);
    @Test
    public void loadGraph() {
        this.graph.setDirected(true);
        this.addNodes("hongkong", "thailand", 3);
        this.addNodes("thailand", "singapore", 1);
        this.addNodes("hongkong", "malaysia", 4);
        this.addNodes("malaysia", "singapore", 1);
        this.addNodes("hongkong", "singapore", 4);
        this.addNodes("singapore", "hongkong", 1);
        this.addNodes("thailand", "hongkong", 2);
        this.addNodes("thailand", "malaysia", 2);
        this.addNodes("singapore", "japan", 10);
        System.out.println(this.graph.toString());
    }

    public void addNodes(String n1Val, String n2Val, int weight) {
        Node<String> n1 = this.graph.addNode(n1Val);
        Node<String> n2 = this.graph.addNode(n2Val);
        this.graph.addEdge(new Edge(n1, n2, weight));
    }

    public void neo4jTest() {
        EmbedNeo4j e = null;
        try {
            e = new EmbedNeo4j();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to initialize database: " + ex.getMessage());
        }
        e.createDb();
        e.shutDown();
    }

    @Test
    public void neo4jRestTest() {
        RestNeo4j e = null;
        try {
            e = new RestNeo4j();
            e.createExampleNodes();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to initialize database: " + ex.getMessage());
        }
    }
}
