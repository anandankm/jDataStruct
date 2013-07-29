package com.grooveshark.jdatastruct.graph.sample;

import org.apache.log4j.Logger;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;

import static org.neo4j.helpers.collection.IteratorUtil.addToCollection;
import static org.neo4j.helpers.collection.MapUtil.map;

import java.util.Map;
import java.util.Iterator;
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
    private String mutualQueryString = "";
    private String followingQueryString = "";
    private String followerQueryString = "";

    private int baseUserid;
    private Neo4jRest server;
    private List<Node> mutualNodes;
    private List<Node> followingNodes;
    private List<Node> followerNodes;
    private List<Node> otherNodes;
    private Map<String, Object> params;


    public QueryProcessor(int baseUserid, String query) {
        this.baseUserid = baseUserid;
        this.query = query;
        this.params = map("indexQuery", this.query, "baseUserid", this.baseUserid);
        /**
         * TODO: Union these queries into a single query.
         *
         * with each query having a weight like
         *
         * RETURN root, "1" as weight
         * RETURN root, "2" as weight
         * ...
         * ...
         * and order by weight
         */
        this.mutualQueryString = "START root=node:users({indexQuery}), base=node({baseUserid}) match base-->root,root-->base RETURN root";
        this.followingQueryString = "START root=node:users({indexQuery}), base=node({baseUserid}) match base-->root where not(root-->base) RETURN root";
        this.followerQueryString = "START root=node:users({indexQuery}), base=node({baseUserid}) match root-->base where not(base-->root) RETURN root";
    }

    public QueryProcessor(int baseUserid, String query, Neo4jRest server) {
        this(baseUserid, query);
        this.server = server;
    }

    public void initializeResult() {
        this.mutualNodes = new LinkedList<Node>();
        this.followingNodes = new LinkedList<Node>();
        this.followerNodes = new LinkedList<Node>();
        this.otherNodes = new LinkedList<Node>();
    }

    public void traverseCypher() {
        this.initializeResult();
        Iterator<Node> nodes = this.server.getMatchingNodes(this.mutualQueryString, this.params);
        addToCollection(nodes, this.mutualNodes);
        System.out.println("mutual size: " + this.mutualNodes.size());
        nodes = this.server.getMatchingNodes(this.followingQueryString, this.params);
        addToCollection(nodes, this.followingNodes);
        System.out.println("following size: " + this.followingNodes.size());
        nodes = this.server.getMatchingNodes(this.followerQueryString, this.params);
        addToCollection(nodes, this.followerNodes);
        System.out.println("follower size: " + this.followerNodes.size());
    }

    public void traverseOneDeg() {
        this.initializeResult();
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
                } else {
                    this.otherNodes.add(node);
                }
                System.out.println("Node property username: " + node.getProperty("username"));
            }
        } finally {
            hits.close();
        }
        System.out.println("mutual size: " + this.mutualNodes.size());
        System.out.println("following size: " + this.followingNodes.size());
        System.out.println("follower size: " + this.followerNodes.size());
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
