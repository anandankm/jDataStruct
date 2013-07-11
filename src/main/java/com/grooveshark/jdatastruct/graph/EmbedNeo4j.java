package com.grooveshark.jdatastruct.graph;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.tooling.GlobalGraphOperations;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.HashMap;
import java.io.BufferedWriter;
import com.grooveshark.util.FileUtils;

public class EmbedNeo4j
{

    public static final Logger log = Logger.getLogger(EmbedNeo4j.class);
    private static enum RelTypes implements RelationshipType { KNOWS };

    private String db_path = "";

    private GraphDatabaseService graphDb;
    private Node firstNode;
    private Node secondNode;
    private Relationship relationship;

    public EmbedNeo4j(String db_path) {
        this.db_path = db_path;
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( this.db_path );
        registerShutdownHook(this.graphDb);
    }

    public void createDb() {
        Transaction tx = this.graphDb.beginTx();
        try {
            System.out.println("Graph database created...");
            this.firstNode = graphDb.createNode();
            this.secondNode = graphDb.createNode();
            this.firstNode.setProperty( "userid", 1 );
            this.secondNode.setProperty( "userid", 2 );
            this.relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
            this.relationship.setProperty( "type", "follows");
            System.out.println("Data inserted into graphDb...");
            System.out.println("First Node: (userid)" + this.firstNode.getProperty( "userid" ));
            System.out.println("Second Node: (userid)" + this.secondNode.getProperty( "userid" ));
            System.out.println("Relationship: (type)" + this.relationship.getProperty( "type" ));
            tx.success();
        } finally {
            tx.finish();
        }
    }

    public <T extends PropertyContainer> String getSinglePropertyKey(T container) throws RuntimeException {
        Iterator<String> keysItr = container.getPropertyKeys().iterator();
        String key = "";
        int numKeys = 0;
        while (keysItr.hasNext()) {
            numKeys++;
            if (numKeys > 1) {
                throw new RuntimeException("Number of keys > 1. Current key: " + keysItr.next() + "; Previous key: " + key);
            }
            key = (String) keysItr.next();
        }
        return key;
    }

    public void checkIndexHits() throws Exception {
        Index<Node> usersIndex = this.graphDb.index().forNodes("users");
        IndexHits<Node> hits = usersIndex.query("userid:[0 TO 300000000]");
        System.out.println("Node size: " + hits.size());
    }

    public void lookupSize() throws Exception {
        GlobalGraphOperations ggo = GlobalGraphOperations.at(this.graphDb);
        Iterator<Node> itr = ggo.getAllNodes().iterator();
        HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        int nodeSize = 0;
        int relSize = 0;
        BufferedWriter nodeWriter = FileUtils.getWriter( "node_file_2" );
        BufferedWriter relWriter = FileUtils.getWriter( "rel_file_2" );
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            String nodeKey = this.getSinglePropertyKey(node);
            if (nodeKey.length() == 0) {
                System.out.println("Node key not present for nodeId: " + node.getId());
                continue;
            }
            String nodeString = node.getId() + "\t" + nodeKey + "\t" + node.getProperty( nodeKey );
            FileUtils.writeLine(nodeWriter, nodeString);
            nodeSize++;
            Iterator<Relationship> relItr = node.getRelationships().iterator();
            while (relItr.hasNext()) {
                Relationship rel = (Relationship) relItr.next();
                relSize++;
                Node startNode = rel.getStartNode();
                String startNodeKey = this.getSinglePropertyKey(startNode);
                Node endNode = rel.getEndNode();
                String endNodeKey = this.getSinglePropertyKey(endNode);
                String relKey = this.getSinglePropertyKey(rel);
                String relString = rel.getId() + "\t" + startNode.getId()  + "\t" + startNode.getProperty( startNodeKey ) + "\t" + relKey + "\t" + rel.getProperty( relKey ) + "\t" + endNode.getId() + "\t" + endNode.getProperty( endNodeKey );
                FileUtils.writeLine(relWriter, relString);
            }
            if (nodeSize%500 == 0) {
                System.out.println("Node size: " + nodeSize);
            }
        }
        System.out.println("Number of nodes: " + nodeSize);
        System.out.println("Number of relationships: " + relSize);
    }

    public int deleteNode(Node node) {
        int numRel = 0;
        Transaction tx = this.graphDb.beginTx();
        try {
            Iterator<Relationship> relItr = node.getRelationships().iterator();
            while (relItr.hasNext()) {
                Relationship rel = (Relationship) relItr.next();
                Node startNode = rel.getStartNode();
                Node endNode = rel.getEndNode();
                rel.delete();
                numRel++;
            }
            node.delete();
            tx.success();
        } finally {
            tx.finish();
        }
        return numRel;
    }

    public void removeData() {
        GlobalGraphOperations ggo = GlobalGraphOperations.at(this.graphDb);
        Iterator<Node> itr = ggo.getAllNodes().iterator();
        HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        int nodeSize = 0;
        int relSize = 0;
        System.out.println("Removing Data..");
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            relSize += this.deleteNode(node);
            nodeSize++;
            if (nodeSize%500 == 0) {
                System.out.println("Node size: " + nodeSize);
                System.out.println("Relationship size: " + relSize);
            }
        }
        System.out.println("Number of nodes: " + nodeSize);
        System.out.println("Number of relationships: " + relSize);
        relSize = 0;
        Iterator<Relationship> relItr = ggo.getAllRelationships().iterator();
        while (relItr.hasNext()) {
            Relationship rel = (Relationship) relItr.next();
            relSize ++;
        }
        System.out.println("Number of relationships: " + relSize);
        System.out.println("Data removed.");
    }

    public void shutDown() {
        System.out.println("Shutting down graph database...");
        graphDb.shutdown();
        System.out.println("Graph database shutdown.");
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook( new Thread()
                {
                    @Override
                    public void run() {
                        graphDb.shutdown();
                    }
                } );
    }

}
