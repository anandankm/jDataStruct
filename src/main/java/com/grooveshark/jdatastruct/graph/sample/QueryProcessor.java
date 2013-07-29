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

    private String startQuery = "";
    private String mutualQueryString = "";
    private String mutualMutualQueryString = "";
    private String mutualFollowingQueryString = "";
    private String mutualFollowerQueryString = "";
    private String followingQueryString = "";
    private String followingMutualQueryString = "";
    private String followingFollowingQueryString = "";
    private String followingFollowerQueryString = "";
    private String followerQueryString = "";
    private String followerMutualQueryString = "";
    private String followerFollowingQueryString = "";
    private String followerFollowerQueryString = "";

    private int baseUserid;
    private Neo4jRest server;
    private Map<String, Object> params;

    private List<Node> mutualNodes;
    private List<Node> mutualMutualNodes;
    private List<Node> mutualFollowingNodes;
    private List<Node> mutualFollowerNodes;
    private List<Node> followingNodes;
    private List<Node> followingMutualNodes;
    private List<Node> followingFollowingNodes;
    private List<Node> followingFollowerNodes;
    private List<Node> followerNodes;
    private List<Node> followerMutualNodes;
    private List<Node> followerFollowingNodes;
    private List<Node> followerFollowerNodes;
    private List<Node> otherNodes;

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
        this.startQuery = "START root=node:users({indexQuery}), base=node({baseUserid}) ";
        this.mutualQueryString = this.startQuery +
            "match base-->root,root-->base RETURN root";
        this.mutualMutualQueryString = this.startQuery +
            "match base-->root-->foaf,foaf-->root,root-->base RETURN foaf";
        this.mutualFollowingQueryString = this.startQuery +
            "match base-->root-->foaf,root-->base where not(foaf-->root) RETURN foaf";
        this.mutualFollowerQueryString = this.startQuery +
            "match base-->root<--foaf,root-->base where not(root-->foaf) RETURN foaf";
        this.followingQueryString = this.startQuery +
            "match base-->root where not(root-->base) RETURN root";
        this.followingMutualQueryString = this.startQuery +
            "match base-->root-->foaf, foaf-->root where not(foaf<-->base) and not(root-->base)  RETURN foaf";
        this.followingFollowingQueryString = this.startQuery +
            "match base-->root-->foaf where not(foaf<-->base) and not(foaf-->root) and not(root-->base)  RETURN foaf";
        this.followingFollowerQueryString = this.startQuery +
            "match base-->root<--foaf where not(root-->base) and not(root-->foaf) and not(base-->foaf) and id(foaf) <> {baseUserid} RETURN foaf";
        this.followerQueryString = this.startQuery +
            "match base<--root where not(base-->root) RETURN root";
    }

    public QueryProcessor(int baseUserid, String query, Neo4jRest server) {
        this(baseUserid, query);
        this.server = server;
    }

    public void initializeResult() {
        this.mutualNodes = new LinkedList<Node>();
        this.mutualMutualNodes = new LinkedList<Node>();
        this.mutualFollowingNodes = new LinkedList<Node>();
        this.mutualFollowerNodes = new LinkedList<Node>();
        this.followingNodes = new LinkedList<Node>();
        this.followingMutualNodes = new LinkedList<Node>();
        this.followingFollowingNodes = new LinkedList<Node>();
        this.followingFollowerNodes = new LinkedList<Node>();
        this.followerNodes = new LinkedList<Node>();
        this.followerMutualNodes = new LinkedList<Node>();
        this.followerFollowingNodes = new LinkedList<Node>();
        this.followerFollowerNodes = new LinkedList<Node>();
        this.otherNodes = new LinkedList<Node>();
    }

    public void traverseCypher() {
        this.initializeResult();
        Iterator<Node> nodes = this.server.getMatchingNodes(this.mutualQueryString, this.params);
        addToCollection(nodes, this.mutualNodes);
        System.out.println("Mutual size: " + this.mutualNodes.size());

        nodes = this.server.getMatchingNodes(this.mutualMutualQueryString, this.params);
        addToCollection(nodes, this.mutualMutualNodes);
        System.out.println("Mutual Mutual size: " + this.mutualMutualNodes.size());

        nodes = this.server.getMatchingNodes(this.mutualFollowingQueryString, this.params);
        addToCollection(nodes, this.mutualFollowingNodes);
        System.out.println("Mutual Following size: " + this.mutualFollowingNodes.size());

        nodes = this.server.getMatchingNodes(this.mutualFollowerQueryString, this.params);
        addToCollection(nodes, this.mutualFollowerNodes);
        System.out.println("Mutual Follower size: " + this.mutualFollowerNodes.size());

        nodes = this.server.getMatchingNodes(this.followingQueryString, this.params);
        addToCollection(nodes, this.followingNodes);
        System.out.println("Following size: " + this.followingNodes.size());

        nodes = this.server.getMatchingNodes(this.followingMutualQueryString, this.params);
        addToCollection(nodes, this.followingMutualNodes);
        System.out.println("Following Mutual size: " + this.followingMutualNodes.size());

        nodes = this.server.getMatchingNodes(this.followingFollowingQueryString, this.params);
        addToCollection(nodes, this.followingFollowingNodes);
        System.out.println("Following Following size: " + this.followingFollowingNodes.size());

        nodes = this.server.getMatchingNodes(this.followingFollowerQueryString, this.params);
        addToCollection(nodes, this.followingFollowerNodes);
        System.out.println("Following Follower size: " + this.followingFollowerNodes.size());

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
