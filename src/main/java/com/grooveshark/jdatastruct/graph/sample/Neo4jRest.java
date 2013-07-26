package com.grooveshark.jdatastruct.graph.sample;

import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.ExecutingRestAPI;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.RestRequest;
import org.neo4j.rest.graphdb.batch.RecordingRestRequest;
import org.neo4j.rest.graphdb.batch.BatchRestAPI;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.batch.RestOperations;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.entity.RestEntity;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.index.RestRelationshipIndex;
import org.apache.log4j.Logger;

import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.index.lucene.QueryContext;

import com.sun.jersey.api.client.ClientResponse;
import com.grooveshark.util.FileUtils;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.lang.RuntimeException;

import static org.neo4j.helpers.collection.MapUtil.map;

public class Neo4jRest
{

    public static final Logger log = Logger.getLogger(Neo4jRest.class);

    private static enum RelTypes implements RelationshipType { KNOWS };
    private String serverRootUri = null;
    private RestAPIFacade restAPI = null;
    private BatchRestAPI batchRestAPI = null;
    private RestRequest restRequest = null;
    private RestGraphDatabase graphDb = null;
    private RestIndex<Node> nodeIndex = null;
    private RestIndex<Relationship> relIndex = null;
    private String nodeKey = null;
    private String indexTextKey = null;
    private String nodeIndexUniquePath = null;
    private String relIndexUniquePath = null;

    private String url = null;

    public Neo4jRest(String serverRootUri) {
        this.serverRootUri = serverRootUri;
        this.restAPI = new RestAPIFacade(this.serverRootUri);
        this.batchRestAPI = new BatchRestAPI(this.serverRootUri, this.restAPI);
        this.restRequest = this.batchRestAPI.getRestRequest();
        this.graphDb = new RestGraphDatabase(this.restAPI);
    }

    public Neo4jRest(String serverRootUri, String nodeInd)
        throws Neo4jRestException
    {
        this(serverRootUri);
        this.setNodeIndex(nodeInd);
    }

    public Neo4jRest(String serverRootUri, String nodeInd, String relInd)
        throws Neo4jRestException
    {
        this(serverRootUri);
        this.setNodeIndex(nodeInd);
        this.setRelIndex(relInd);
    }

    public void setNodeIndex(String nodeInd)
        throws Neo4jRestException
    {
        if (this.batchRestAPI == null) {
            throw new Neo4jRestException("Batch Rest API is not instantiated");
        }
        this.nodeIndex = this.batchRestAPI.index().forNodes(nodeInd);
        this.nodeIndexUniquePath = this.nodeIndex.indexPath() + "?uniqueness=get_or_create";
    }

    public void setRelIndex(String relInd)
        throws Neo4jRestException
    {
        if (this.batchRestAPI == null) {
            throw new Neo4jRestException("Batch Rest API is not instantiated");
        }
        this.relIndex = (RestIndex<Relationship>) this.batchRestAPI.index().forRelationships(relInd);
        this.relIndexUniquePath = this.relIndex.indexPath() + "?uniqueness=get_or_create";
    }

    public void checkIndexHits(String query) throws Exception {
        IndexHits<Node> hits = this.nodeIndex.query(query);
        try {
            System.out.println("Node size: " + hits.size());
        } finally {
            hits.close();
        }
    }

    public void addToRestRequest(GNode node) {
        Map<String, Object> nodeData = map("key", this.nodeKey, "value", node.userid, "properties", node.getProps());
        RequestResult result = this.restRequest.post(this.nodeIndexUniquePath, nodeData);
        RestNode restNode = this.batchRestAPI.createRestNode(result);
        RestEntity restEntity = (RestEntity) restNode;
        String uri = restEntity.getUri();
        Map<String, Object> data = map("key", this.indexTextKey, "value", node.getIndexText(), "uri", uri);
        System.out.println("node post data:");
        System.out.println(nodeData);
        System.out.println("index post data:");
        System.out.println(data);
        this.restRequest.post(this.nodeIndex.indexPath(), data);
    }

    public void batchInsert(List<GNode> nodes) {
        for (GNode node :  nodes) {
            this.addToRestRequest(node);
        }
        //this.batchRestAPI.executeBatchRequest();
    }

    /**
     * Setter method for nodeKey
     */
    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }
    /**
     * Getter method for nodeKey
     */
    public String getNodeKey() {
        return this.nodeKey;
    }
    /**
     * Setter method for indexTextKey
     */
    public void setIndexTextKey(String indexTextKey) {
        this.indexTextKey = indexTextKey;
    }
    /**
     * Getter method for indexTextKey
     */
    public String getIndexTextKey() {
        return this.indexTextKey;
    }
}
