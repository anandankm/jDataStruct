package com.grooveshark.jdatastruct.graph.sample;


import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;

import org.apache.log4j.Logger;
import com.grooveshark.util.StringUtils;

public class QueryProcessorTest 
{
    public static final Logger log = Logger.getLogger(QueryProcessorTest.class);

    public static final String SERVER_URI = "http://localhost:7474/db/data";

    private Neo4jRest server;
    private QueryProcessor processor;

    @Before
    public void setup() {
        try {
            System.out.println("Setting up server");
            this.server = new Neo4jRest(SERVER_URI, GNode.NODE_INDEX, GEdge.REL_INDEX);
            this.server.setNodeKey(GNode.USERID_KEY);
            this.server.setRelKey(GEdge.EDGE_INDEX_KEY);
            this.server.setRelProps(GEdge.REL_PROPS);
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
            fail("Failed to test simple query: " + ex.getMessage());
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
            fail("Failed to test Cypher query for foaf: " + ex.getMessage());
        }
    }

}
