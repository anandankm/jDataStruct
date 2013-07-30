package com.grooveshark.jdatastruct.graph.sample;


import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;

import org.apache.log4j.Logger;
import com.grooveshark.util.StringUtils;

public class SampleNodeTest
{
    public static final Logger log = Logger.getLogger(SampleNodeTest.class);

    public static final String NODE_FILE = "data/node_file";
    public static final String LOCATION_FILE = "data/location_file";
    public static final String SERVER_URI = "http://localhost:7474/db/data";

    private NodeParser parser;
    private Neo4jRest server;

    @Before
    public void nodeParser() {
        try {
            System.out.println("Parsing node & location files");
            this.parser = new NodeParser(NODE_FILE, LOCATION_FILE);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test node parser: " + ex.getMessage());
        }
    }

    @Test
    public void testNeo4jRestBatchInsert() {
        try {
            System.out.println("Setting up server");
            this.server = new Neo4jRest(SERVER_URI, GNode.NODE_INDEX, GEdge.REL_INDEX);
            this.server.setNodeKey(GNode.USERID_KEY);
            this.server.setRelKey(GEdge.EDGE_INDEX_KEY);
            this.server.setRelProps(GEdge.REL_PROPS);
            long start = System.currentTimeMillis();
            System.out.println("Creating batch insert request");
            this.server.batchInsert(this.parser.nodes);
            this.server.batchRelInsert(this.parser.edges);
            this.server.executeBatch();
            float elapsed = (System.currentTimeMillis() - start)/(float) 1000;
            System.out.println("Done. [" + elapsed + " secs]");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

}
