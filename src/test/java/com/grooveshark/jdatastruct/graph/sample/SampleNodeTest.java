package com.grooveshark.jdatastruct.graph.sample;


import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.neo4j.helpers.collection.MapUtil.map;

import org.apache.log4j.Logger;
import com.grooveshark.util.StringUtils;

public class SampleNodeTest
{
    public static final Logger log = Logger.getLogger(SampleNodeTest.class);

    public static final String NODE_FILE = "data/node_file";
    public static final String LOCATION_FILE = "data/location_file";
    public static final String SERVER_URI = "http://localhost:7474/db/data";
    public static final String NODE_INDEX = "users";
    public static final String REL_INDEX = "followers";
    public static final String NODE_KEY = "userid";
    public static final String REL_KEY = "edge";
    public static final Map<String, Object> REL_PROPS = map("name", "follows");

    private NodeParser parser;

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
            Neo4jRest server = new Neo4jRest(SERVER_URI, NODE_INDEX, REL_INDEX);
            server.setNodeKey(NODE_KEY);
            server.setRelKey(REL_KEY);
            server.setRelProps(REL_PROPS);
            long start = System.currentTimeMillis();
            System.out.println("Creating batch insert request");
            server.batchInsert(this.parser.nodes);
            server.batchRelInsert(this.parser.edges);
            server.executeBatch();
            float elapsed = (System.currentTimeMillis() - start)/(float) 1000;
            System.out.println("Done. [" + elapsed + " secs]");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

}
