package com.grooveshark.jdatastruct.graph.sample;

import org.apache.log4j.Logger;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.index.lucene.QueryContext;

import java.util.List;
import java.util.LinkedList;

import com.grooveshark.util.FileUtils;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;
import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;


public class QueryProcessor 
{

    public static final Logger log = Logger.getLogger(QueryProcessor.class);

    private static enum RelTypes implements RelationshipType { KNOWS };
    private String query = "";

    private int baseUserid;
    private Neo4jRest server;
    private List<Node> mutualNodes = new LinkedList<Node>();
    private List<Node> followingNodes = new LinkedList<Node>();
    private List<Node> followerNodes = new LinkedList<Node>();


    public QueryProcessor(int baseUserid, String query) {
        this.baseUserid = baseUserid;
        this.query = query;
    }

    public QueryProcessor(int baseUserid, String query, Neo4jRest server) {
        this.baseUserid = baseUserid;
        this.query = query;
        this.server = server;
    }

    public void processQuery()
        throws Neo4jRestException
    {
        Node baseNode = this.server.getSingleNode(GNode.USERID_KEY, this.baseUserid);
        IndexHits<Node> hits = this.server.getNodeIndexHits(this.query);
        try {
            System.out.println("Hits size: " + hits.size());
            for (Node node : hits) {
                long id = node.getId();
                String following = this.baseUserid + "-" + id;
                String follower = id + "-" + this.baseUserid;
                boolean followingExists = this.server.relExists(GEdge.EDGE_INDEX_KEY, following);
                boolean followerExists = this.server.relExists(GEdge.EDGE_INDEX_KEY, follower);
                if (followingExists && followerExists) {
                    this.mutualNodes.add(node);
                } else if (followingExists && !followerExists) {
                    this.followingNodes.add(node);
                } else if (!followingExists && followerExists) {
                    this.followerNodes.add(node);
                }
            }
            System.out.println("mutual size: " + this.mutualNodes.size());
            System.out.println("following size: " + this.followingNodes.size());
            System.out.println("follower size: " + this.followerNodes.size());
        } finally {
            hits.close();
        }

    }

    /**
     * Setter method for server
     */
    public void setServer(Neo4jRest server) {
        this.server = server;
    }
    /**
     * Getter method for server
     */
    public Neo4jRest getServer() {
        return this.server;
    }
    /**
     * Setter method for query
     */
    public void setQuery(String query) {
        this.query = query;
    }
    /**
     * Getter method for query
     */
    public String getQuery() {
        return this.query;
    }
    /**
     * Setter method for baseUserid
     */
    public void setBaseUserid(int baseUserid) {
        this.baseUserid = baseUserid;
    }
    /**
     * Getter method for baseUserid
     */
    public int getBaseUserid() {
        return this.baseUserid;
    }
}
