package com.grooveshark.jdatastruct.graph;

import java.sql.Types;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
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
            String query = "userid:[0 TO 100]";
            //e.createDb();
            //e.removeData();
            //e.lookupSize();
            e.checkIndexHits(query);
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

    public void neo4jRestIndexTest() {
        try {
            //this.e.checkIndex(1, 20);
            //this.e.checkIndex(1, 21);
            //this.e.checkIndexHits();
            //this.e.countRelationships();
            this.e.deleteAll();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to check index: " + ex.getMessage());
        }
    }

    @Test
    public void neo4jRestBatchTest() {
        try {
            String nodeIndex = "users";
            String relIndex = "followers";
            String key = "userid";
            Map<String, Object> startProps = new HashMap<String, Object>();
            startProps.put("username", "paulo");
            startProps.put("fname", "Paulo Da Silva");
            startProps.put("lname", "");
            startProps.put("mname", "R");
            startProps.put("city", "Sao Paulo");
            startProps.put("state", "");
            startProps.put("country", "BR");
            startProps.put("email", "paulothesilva@gmail.com");
            long zip = 32607L;
            startProps.put("zip", zip);
            String isactive = "false";
            boolean isact = Boolean.parseBoolean(isactive);
            startProps.put("isactive", isact);
            Map<String, Object> endProps = new HashMap<String, Object>();
            endProps.put("username", "josh");
            endProps.put("fname", "Josh Greenberg");
            endProps.put("lname", "");
            endProps.put("mname", "");
            endProps.put("city", "Gainesville");
            endProps.put("state", "FL");
            endProps.put("country", "US");
            endProps.put("email", "josh.greenberg@escapemg.com");
            endProps.put("zip", zip);
            this.e.batchInsertCheck(nodeIndex, relIndex, key, 1, startProps, 2, endProps);
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
