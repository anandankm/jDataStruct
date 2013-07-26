package com.grooveshark.jdatastruct.graph.sample;


import org.junit.Test;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;

public class SampleNodeTest
{
    public static final Logger log = Logger.getLogger(SampleNodeTest.class); 

    public static final String nodeFile = "data/node_file";
    @Test
    public void testNodeParser() {
        try {
            NodeParser parser = new NodeParser(nodeFile);
            parser.displayNodes();
            parser.displayEdges();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test node parser: " + ex.getMessage());
        }
    }

}
