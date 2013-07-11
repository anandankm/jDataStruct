package com.grooveshark.jdatastruct.graph;

import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.ExecutingRestAPI;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.batch.BatchRestAPI;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.index.RestRelationshipIndex;
import org.apache.log4j.Logger;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.index.lucene.QueryContext;

import com.sun.jersey.api.client.ClientResponse;
import com.grooveshark.util.FileUtils;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.lang.RuntimeException;

import static org.neo4j.helpers.collection.MapUtil.map;

public class RestNeo4j
{

    public static final Logger log = Logger.getLogger(RestNeo4j.class);

    private static enum RelTypes implements RelationshipType { KNOWS };
    private static final String SERVER_ROOT_URI = "http://172.16.100.198:7474/db/data";
    private RestAPIFacade restAPI = null;
    private BatchRestAPI batchRestAPI = null;

    private String url = null;

    public RestNeo4j() {
        this.restAPI = new RestAPIFacade(SERVER_ROOT_URI);
        this.batchRestAPI = new BatchRestAPI(SERVER_ROOT_URI, this.restAPI);
    }

    public void createExampleNodes() {
        RestNode user1Node = this.restAPI.getOrCreateNode(this.restAPI.index().forNodes("users"), "userid", 1, null);
        RestNode user2Node = this.restAPI.getOrCreateNode(this.restAPI.index().forNodes("users"), "userid", 2, null);
        System.out.println("User1Node property userid: " + user1Node.getProperty("userid"));
        System.out.println("User2Node property userid: " + user2Node.getProperty("userid"));
        Iterator<Relationship> itr = user1Node.getRelationships().iterator();
        while (itr.hasNext()) {
            Relationship rel = (Relationship) itr.next();
            System.out.println("User1Node has a relationship with property: " + rel.getProperty( "type" ));
        }
    }

    public void checkIndexHits() throws Exception {
        RestIndex<Node> usersIndex = this.restAPI.index().forNodes("users");
        IndexHits<Node> hits = usersIndex.query(QueryContext.numericRange("userid", 0, 100));
        System.out.println("Node size: " + hits.size());
    }

    public void checkIndex(int uid1, int uid2) {
        RestIndex<Node> nodeIndex = this.restAPI.index().forNodes("users");
        RestIndex<Relationship> relIndex = (RestIndex<Relationship>) this.restAPI.index().forRelationships("followers");
        RestNode userid1 = this.restAPI.getOrCreateNode(nodeIndex, "userid", uid1, null);
        RestNode userid20 = this.restAPI.getOrCreateNode(nodeIndex, "userid", uid2, null);
        String edgeValue = uid1 + "" + uid2;
        RestRelationship restRel = this.restAPI.getOrCreateRelationship(
                relIndex, "edge", edgeValue, userid1, userid20,"KNOWS" , map("name", "follows"));
        System.out.println("Relationship :- " + restRel.getProperty("name"));
        System.out.println("RelationshipType :- " + restRel.getType().name());
        System.out.println("StartNode :- " + restRel.getStartNode().getProperty("userid"));
        System.out.println("EndNode :- " + restRel.getEndNode().getProperty("userid"));
        System.out.println("Trying out indexhits..");
        IndexHits<Relationship> hits = relIndex.get("edge", edgeValue);
        try {
            for (Relationship rela : hits) {
                System.out.println("Relationship :- " + rela.getProperty("name"));
                System.out.println("RelationshipType :- " + rela.getType().name());
                System.out.println("StartNode :- " + rela.getStartNode().getProperty("userid"));
                System.out.println("EndNode :- " + rela.getEndNode().getProperty("userid"));
            }
        } finally {
            hits.close();
        }
    }
    public void countRelationships() throws Exception {
        RestGraphDatabase restGraph = new RestGraphDatabase(this.restAPI);
        Iterator<Node> itr = restGraph.getAllNodes().iterator();
        int relSize = 0;
        int nodeSize = 0;
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            for(Relationship rel : node.getRelationships(Direction.OUTGOING)) {
                relSize++;
            }
            nodeSize++;
            if (nodeSize%500 == 0) {
                System.out.println("Node size: " + nodeSize);
                System.out.println("Rela size: " + relSize);
            }
        }
        System.out.println("Number of nodes: " + nodeSize);
        System.out.println("Number of relationships: " + relSize);
    }


    public void lookupSize() throws Exception {
        RestGraphDatabase restGraph = new RestGraphDatabase(this.restAPI);
        Iterator<Node> itr = restGraph.getAllNodes().iterator();
        HashMap<Integer, Node> nodeMap = new HashMap<Integer, Node>();
        int nodeSize = 0;
        int relSize = 0;
        BufferedWriter nodeWriter = FileUtils.getWriter( "node_file_2" );
        BufferedWriter relWriter = FileUtils.getWriter( "rel_file_2" );
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            String nodeKey = this.getSinglePropertyKey(node);
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

    public void traverseAll() {
        RestGraphDatabase restGraph = new RestGraphDatabase(this.restAPI);
        Iterator<Node> itr = restGraph.getAllNodes().iterator();
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            Iterator<String> keysItr = node.getPropertyKeys().iterator();
            while (keysItr.hasNext()) {
                String key = (String) keysItr.next();
                System.out.println("UserNode property key: " + key);
                System.out.println("UserNode property " + key + ": " + node.getProperty( key ));
            }
            Iterator<Relationship> relItr = node.getRelationships().iterator();
            while (relItr.hasNext()) {
                Relationship rel = (Relationship) relItr.next();
                System.out.println("User1Node has a relationship with property: " + rel.getProperty( this.getSinglePropertyKey(rel) ));
            }
        }
    }

    public void deleteAll() {
        RestGraphDatabase restGraph = new RestGraphDatabase(this.restAPI);
        Iterator<Node> itr = restGraph.getAllNodes().iterator();
        while (itr.hasNext()) {
            Node node = (Node) itr.next();
            Iterator<String> keysItr = node.getPropertyKeys().iterator();
            while (keysItr.hasNext()) {
                String key = (String) keysItr.next();
                System.out.println("UserNode property key: " + key);
                System.out.println("UserNode property " + key + ": " + node.getProperty( key ));
            }
            Iterator<Relationship> relItr = node.getRelationships().iterator();
            while (relItr.hasNext()) {
                Relationship rel = (Relationship) relItr.next();
                System.out.println("User1Node has a relationship with property: " + rel.getProperty( this.getSinglePropertyKey(rel)  ));
                rel.delete();
            }
            node.delete();
        }
    }

    public void batchInsertCheck(final String index, final String key, final Integer node, final List<Integer> knowsNodes) {
        BatchResult batchResult = this.restAPI.executeBatch( new BatchCallback<BatchResult>() {
            @Override
            public BatchResult recordBatch(RestAPI batchRestAPI) {
                BatchResult batchResult = new BatchResult();
                batchResult.insertedNodes = new LinkedList<RestNode>();
                RestIndex<Node> restIndex = batchRestAPI.index().forNodes(index);
                final Map<String, Object> data = map("key", key, "value", node, "properties", null);
                System.out.println("data: " + data);
                BatchRestAPI eRestAPI = new BatchRestAPI(batchRestAPI.getBaseUri(), new RestAPIFacade(batchRestAPI.getBaseUri()));
                final RequestResult result = eRestAPI.getRestRequest().post(restIndex.uniqueIndexPath(), data);
                System.out.println("Status: " + result.getStatus());
                return batchResult;
            }
        });
    }

    public void batchInsertKnows(final String index, final String key, final Integer node, final List<Integer> knowsNodes) {
        BatchResult batchResult = this.restAPI.executeBatch( new BatchCallback<BatchResult>() {
            @Override
            public BatchResult recordBatch(RestAPI batchRestAPI) {
                BatchResult batchResult = new BatchResult();
                batchResult.insertedNodes = new LinkedList<RestNode>();
                RestNode parentNode = batchRestAPI.getOrCreateNode(batchRestAPI.index().forNodes(index), key, node, null);
                for (Integer knowsn: knowsNodes) {
                    RestNode knowsNode = batchRestAPI.getOrCreateNode(batchRestAPI.index().forNodes(index), key, knowsn, null);
                    Relationship rel = parentNode.createRelationshipTo(knowsNode, RelTypes.KNOWS);
                    rel.setProperty( "name", "follows" );
                    batchResult.insertedNodes.add(knowsNode);
                    batchResult.size++;
                }
                batchResult.allRelationships = parentNode.getRelationships();
                return batchResult;
            }
        });
    }

    public class BatchResult {
        public List<RestNode> insertedNodes;
        public int size;
        public Iterable<Relationship> allRelationships;
    }

    /**
     * Setter method for url
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * Getter method for url
     */
    public String getUrl() {
        return this.url;
    }

}
