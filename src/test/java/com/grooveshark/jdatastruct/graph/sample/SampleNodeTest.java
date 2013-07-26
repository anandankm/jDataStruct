package com.grooveshark.jdatastruct.graph.sample;


import org.junit.Test;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import com.grooveshark.util.StringUtils;

public class SampleNodeTest
{
    public static final Logger log = Logger.getLogger(SampleNodeTest.class);

    public static final String nodeFile = "data/node_file";
    public static final String locationFile = "data/location_file";

    @Test
    public void testNodeParser() {
        try {
            NodeParser parser = new NodeParser(nodeFile, locationFile);
            parser.displayNodes();
            parser.displayEdges();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test node parser: " + ex.getMessage());
        }
    }

    public void testNodeFields() {
        try {
            System.out.println("Fields in Node class: " + StringUtils.getFieldNames(GNode.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test node fields: " + ex.getMessage());
        }
    }

    public void testNeo4jRest() {
        try {
            String serverUri = "http://localhost:7474/db/data";
            String nodeInd = "users";
            String nodeKey = "userid";
            String indexTextKey = "indexText";
            Neo4jRest server = new Neo4jRest(serverUri, nodeInd);
            server.setNodeKey(nodeKey);
            server.setIndexTextKey(indexTextKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

}
