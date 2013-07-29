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


    @Test
    public void testQuery() {
        try {
            System.out.println("Setting up server");
            Neo4jRest server = new Neo4jRest(SERVER_URI, NODE_INDEX, REL_INDEX);
            server.setNodeKey(NODE_KEY);
            server.setRelKey(REL_KEY);
            server.setRelProps(REL_PROPS);
            QueryProcessor processor = new QueryProcessor(1, "username:*oni*", server);
            long start = System.currentTimeMillis();
            System.out.println("Querying");
            processor.processQuery();
            float elapsed = (System.currentTimeMillis() - start)/(float) 1000;
            System.out.println("Done. [" + elapsed + " secs]");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test neo4j rest instantiation: " + ex.getMessage());
        }
    }

}
