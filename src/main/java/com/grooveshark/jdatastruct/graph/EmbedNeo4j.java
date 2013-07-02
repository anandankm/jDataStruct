package com.grooveshark.jdatastruct.graph;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import org.apache.log4j.Logger;

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

    public void createIndex() {
    }

    public void removeData() {
        System.out.println("Removing Data..");
        Transaction tx = graphDb.beginTx();
        try {
            this.firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
            this.firstNode.delete();
            this.secondNode.delete();
            tx.success();
            System.out.println("Data removed.");
        } finally {
            tx.finish();
        }
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
