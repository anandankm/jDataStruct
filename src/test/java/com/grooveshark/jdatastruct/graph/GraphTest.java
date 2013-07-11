package com.grooveshark.jdatastruct.graph;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;

public class GraphTest
{

    public Graph<String> graph = new Graph<String>();
    public static final Logger log = Logger.getLogger(GraphTest.class);

    public RestNeo4j e = null;

    @Before
    public void init() {
        try {
            this.e = new RestNeo4j();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to initialize database: " + ex.getMessage());
        }
    }

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
        String db_path = "/home/ldap/anandan.rangasamy/neo4j/test/neo4j-advanced-1.9/data/graph.db";
        try {
            e = new EmbedNeo4j(db_path);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to initialize database: " + ex.getMessage());
        }
        try{
            //e.createDb();
            //e.removeData();
            //e.lookupSize();
            e.checkIndexHits();
            e.shutDown();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to lookup database: " + ex.getMessage());
        }
    }

    public void neo4jRestExampleTest() {
        try {
            this.e.createExampleNodes();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create example nodes: " + ex.getMessage());
        }
    }

    public void neo4jRestlookupTest() {
        try {
            this.e.lookupSize();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to check index: " + ex.getMessage());
        }
    }

    @Test
    public void neo4jRestIndexTest() {
        try {
            //this.e.checkIndex(1, 20);
            //this.e.checkIndex(1, 21);
            //this.e.checkIndexHits();
            this.e.countRelationships();
            //this.e.deleteAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to check index: " + ex.getMessage());
        }
    }

    public void neo4jRestBatchTest() {
        try {
            String index = "users";
            String key = "userid";
            Integer node = 1;
            List<Integer> knowsNodes = new LinkedList<Integer>();
            knowsNodes.add(2);
            knowsNodes.add(3);
            knowsNodes.add(4);
            knowsNodes.add(5);
            knowsNodes.add(6);
            knowsNodes.add(7);
            knowsNodes.add(8);
            knowsNodes.add(9);
            knowsNodes.add(10);
            this.e.batchInsertKnows(index, key, node, knowsNodes);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to insert nodes off of the batch: " + ex.getMessage());
        }
    }

    public void neo4jRestTraverseTest() {
        try {
            this.e.traverseAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to traverse all nodes: " + ex.getMessage());
        }
    }
}
