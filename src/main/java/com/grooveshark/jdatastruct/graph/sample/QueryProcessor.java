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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.TreeSet;

import com.grooveshark.util.FileUtils;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;
import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;
import com.grooveshark.jdatastruct.graph.sample.entities.NodeResult;


public class QueryProcessor 
{

    public static final Logger log = Logger.getLogger(QueryProcessor.class);

    private static enum RelTypes implements RelationshipType { KNOWS };
    private String query = "";

    private static String startQuery = "START root=node:users({indexQuery}), base=node({baseUserid}) ";
    private static String mutualQueryString = startQuery + "match base-->root,root-->base RETURN root";
    private static String mutualMutualQueryString = startQuery + "match base-->root-->foaf,foaf-->root,root-->base RETURN foaf";
    private static String mutualFollowingQueryString = startQuery + "match base-->root-->foaf,root-->base where not(foaf-->root) RETURN foaf";
    private static String mutualFollowerQueryString = startQuery + "match base-->root<--foaf,root-->base where not(root-->foaf) RETURN foaf";
    private static String followingQueryString = startQuery + "match base-->root where not(root-->base) RETURN root";
    private static String followingMutualQueryString = startQuery + "match base-->root-->foaf, foaf-->root where not(foaf<-->base) and not(root-->base)  RETURN foaf";
    private static String followingFollowingQueryString = startQuery + "match base-->root-->foaf where not(foaf<-->base) and not(foaf-->root) and not(root-->base)  RETURN foaf";
    private static String followingFollowerQueryString = startQuery + "match base-->root<--foaf where not(root-->base) and not(root-->foaf) and not(base-->foaf) and id(foaf) <> {baseUserid} RETURN foaf";
    private static String followerQueryString = startQuery + "match base<--root where not(base-->root) RETURN root";
    private static String followerMutualQueryString = startQuery + "match base<--root-->foaf,foaf-->root where not(base-->root) and not(foaf<-->base) RETURN foaf";
    private static String followerFollowerQueryString = startQuery + "match base<--root<--foaf where not(root-->foaf) and not(base-->root) and not(foaf<-->base) and id(foaf) <> {baseUserid} RETURN foaf";
    private static String followerFollowingQueryString = startQuery + "match base<--root-->foaf where not(foaf-->root) and not(base-->root) and not(foaf<-->base) and id(foaf) <> {baseUserid} RETURN foaf";

    private long baseUserid;
    private GNode baseNode;
    private Neo4jRest server;
    private Map<String, Object> params;
    private Map<Float, String> queryStringWeight;

    private TreeSet<NodeResult> nodeResults = new TreeSet<NodeResult>();
    private List<Node> mutualNodes;
    private List<Node> followingNodes;
    private List<Node> followerNodes;
    private List<Node> otherNodes;

    public QueryProcessor(long baseUserid, String query) {
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
        this.queryStringWeight = new HashMap<Float, String>();
        Float weight = 1.0f;
        this.queryStringWeight.put(weight++, mutualQueryString);
        this.queryStringWeight.put(weight++, followingQueryString);
        this.queryStringWeight.put(weight++, followerQueryString);
        this.queryStringWeight.put(weight++, mutualMutualQueryString);
        this.queryStringWeight.put(weight++, mutualFollowingQueryString);
        this.queryStringWeight.put(weight++, mutualFollowerQueryString);
        this.queryStringWeight.put(weight++, followingMutualQueryString);
        this.queryStringWeight.put(weight++, followingFollowingQueryString);
        this.queryStringWeight.put(weight++, followingFollowerQueryString);
        this.queryStringWeight.put(weight++, followerMutualQueryString);
        this.queryStringWeight.put(weight++, followerFollowerQueryString);
        this.queryStringWeight.put(weight++, followerFollowingQueryString);
    }

    public QueryProcessor(long baseUserid, String query, Neo4jRest server)
        throws Neo4jRestException
    {
        this(baseUserid, query);
        this.server = server;
        this.baseNode = new GNode( this.server.getSingleNode(GNode.USERID_KEY, this.baseUserid) );
    }

    public void initializeResult() {
        this.nodeResults = new TreeSet<NodeResult>();
        this.mutualNodes = new LinkedList<Node>();
        this.followingNodes = new LinkedList<Node>();
        this.followerNodes = new LinkedList<Node>();
        this.otherNodes = new LinkedList<Node>();
    }

    public void fillinNodes(Float weight)
    {
        Iterator<Node> itr = this.server.getMatchingNodes(this.queryStringWeight.get(weight), this.params);
        while (itr.hasNext()) {
            GNode node = new GNode( (Node) itr.next() );
            NodeResult nodeResult = new NodeResult(weight, node);
            nodeResult.setDistanceFromBase(this.baseNode);
            this.nodeResults.add(nodeResult);
        }
    }

    public void traverseCypher() {
        this.initializeResult();
        for (Float weight : this.queryStringWeight.keySet()) {
            this.fillinNodes(weight);
        }
        System.out.println("Node results size: " + this.nodeResults.size());
        Iterator<NodeResult> itr = this.nodeResults.iterator();
        while(itr.hasNext()) {
            NodeResult nodeResult = (NodeResult) itr.next();
            System.out.println(nodeResult.weight + "\t" + nodeResult.node.userid + "\t" + nodeResult.node.username);
        }
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
    public void setBaseUserid(long baseUserid) {
        this.baseUserid = baseUserid;
    }
    /**
     * Getter method for baseUserid
     */
    public long getBaseUserid() {
        return this.baseUserid;
    }
}
