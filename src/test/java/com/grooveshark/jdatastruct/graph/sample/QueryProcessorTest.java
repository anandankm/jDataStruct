package com.grooveshark.jdatastruct.graph.sample;


import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.neo4j.helpers.collection.MapUtil.map;

import org.apache.log4j.Logger;
import com.grooveshark.util.StringUtils;

public class QueryProcessorTest 
{
    public static final Logger log = Logger.getLogger(QueryProcessorTest.class);

    public static final String SERVER_URI = "http://localhost:7474/db/data";
    public static final String NODE_INDEX = "users";
    public static final String REL_INDEX = "followers";
    public static final String NODE_KEY = "userid";
    public static final String REL_KEY = "edge";
    public static final Map<String, Object> REL_PROPS = map("name", "follows");
    private Neo4jRest server;
    private QueryProcessor processor;

    @Before
    public void setup() {
        try {
            System.out.println("Setting up server");
            this.server = new Neo4jRest(SERVER_URI, NODE_INDEX, REL_INDEX);
            this.server.setNodeKey(NODE_KEY);
            this.server.setRelKey(REL_KEY);
            this.server.setRelProps(REL_PROPS);
            int baseUserid = 1;
            String indexQuery = "username:*oni*";
            this.processor = new QueryProcessor(baseUserid, indexQuery, this.server);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

    public void testSimpleQuery() {
        try {
            long start = System.currentTimeMillis();
            System.out.println("Querying");
            this.processor.traverseOneDeg();
            float elapsed = System.currentTimeMillis() - start;
            System.out.println("Done. [" + elapsed + " ms]");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

    @Test
    public void testCypherQuery() {
        try {
            long start = System.currentTimeMillis();
            System.out.println("Querying");
            this.processor.traverseCypher();
            float elapsed = System.currentTimeMillis() - start;
            System.out.println("Done. [" + elapsed + " ms]");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

}
