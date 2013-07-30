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
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
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
import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;

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
    private String relKey = null;
    private String nodeIndexUniquePath = null;
    private String relIndexUniquePath = null;
    private Map<String, Object> relProps = null;
    private static Map<String, String> luceneFullText = null;
    private RestCypherQueryEngine queryEngine = null;

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

    public static Map<String, String> getLuceneFullText() {
        if (Neo4jRest.luceneFullText == null) {
            Neo4jRest.luceneFullText = new HashMap<String, String>();
            Neo4jRest.luceneFullText.put("provider", "lucene");;
            Neo4jRest.luceneFullText.put("type", "fulltext");;
        }
        return Neo4jRest.luceneFullText;
    }

    public void setNodeIndex(String nodeInd)
        throws Neo4jRestException
    {
        if (this.batchRestAPI == null) {
            throw new Neo4jRestException("Batch Rest API is not instantiated");
        }
        this.nodeIndex = this.batchRestAPI.index().forNodes(nodeInd, Neo4jRest.getLuceneFullText());
        this.nodeIndexUniquePath = this.nodeIndex.indexPath() + "?uniqueness=get_or_create";
    }

    public void setRelIndex(String relInd)
        throws Neo4jRestException
    {
        if (this.batchRestAPI == null) {
            throw new Neo4jRestException("Batch Rest API is not instantiated");
        }
        this.relIndex = (RestIndex<Relationship>) this.batchRestAPI.index().forRelationships(relInd, Neo4jRest.getLuceneFullText());
        this.relIndexUniquePath = this.relIndex.indexPath() + "?uniqueness=get_or_create";
    }

    public Node getSingleNode(String key, Object value)
        throws Neo4jRestException
    {
        IndexHits<Node> hits = this.nodeIndex.get(key, value);
        Node result = null;
        try {
            if (hits.size() > 1) {
                throw new Neo4jRestException("Key/Value pair has more than one match. Key: " + key + "; value: " + value);
            }
            result = hits.getSingle();
        } finally {
            hits.close();
        }
        return result;
    }

    public IndexHits<Node> getNodeIndexHits(String query) {
        return this.nodeIndex.query(query);
    }

    public boolean relExists(String key, Object value) {
        IndexHits<Relationship> hits = this.relIndex.get(key, value);
        if (hits.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Iterator<Node> getMatchingNodes(String queryString, Map<String, Object> params) {
        if (this.queryEngine == null) {
            this.queryEngine = new RestCypherQueryEngine(this.restAPI);
        }
        return this.queryEngine.query(queryString, params).to(Node.class).iterator();
    }


    public void checkIndexHits(String query) throws Exception {
        IndexHits<Node> hits = this.nodeIndex.query(query);
        try {
            System.out.println("Node size: " + hits.size());
            for (Node node : hits) {
                System.out.println("userid: " + node.getId());
            }
        } finally {
            hits.close();
        }
    }

    public RestNode addToRestRequest(Map<String, Object> nodeData) {
        RequestResult result = this.restRequest.post(this.nodeIndexUniquePath, nodeData);
        return this.batchRestAPI.createRestNode(result);
    }

    public RestNode addToRestRequest(long userid) {
        Map<String, Object> nodeData = nodeData = map("key", this.nodeKey, "value", userid);
        return this.addToRestRequest(nodeData);
    }

    public RestNode addToRestRequest(long userid, Map<String, Object> props) {
        Map<String, Object> nodeData = map("key", this.nodeKey, "value", userid, "properties", props);
        RestNode restNode = this.addToRestRequest(nodeData);
        this.addPropsToIndex(restNode, props, this.nodeIndex);
        return restNode;
    }

    public <T extends PropertyContainer> void addPropsToIndex(T entity, final Map<String, Object> props, RestIndex<T> index) {
        final RestEntity restEntity = (RestEntity) entity;
        final String uri = restEntity.getUri();
        for (String key : GNode.getIndexKeys()) {
            final Map<String, Object> data = map("key", key, "value", props.get(key), "uri", uri);
            this.restRequest.post(index.indexPath(), data);
        }
    }

    public void batchInsert(List<GNode> nodes) {
        for (GNode node :  nodes) {
            this.addToRestRequest(node.userid, node.getProps());
        }
    }

    public void batchRelInsert(List<GEdge> edges) {
        for (GEdge edge : edges) {
            RestNode startNode = this.addToRestRequest(edge.sUserid);
            RestNode endNode = this.addToRestRequest(edge.eUserid);
            String relValue = edge.sUserid + "-" + edge.eUserid;
            Map<String, Object> relData = map("key", this.relKey, "value", relValue, "properties", this.relProps, "start", startNode.getUri(), "end", endNode.getUri(), "type", "KNOWS");
            this.restRequest.post(this.relIndexUniquePath, relData);
        }
    }

    public void executeBatch() {
        this.batchRestAPI.executeBatchRequest();
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
     * Setter method for relKey
     */
    public void setRelKey(String relKey) {
        this.relKey = relKey;
    }
    /**
     * Getter method for relKey
     */
    public String getRelKey() {
        return this.relKey;
    }
    /**
     * Setter method for relProps
     */
    public void setRelProps(Map<String, Object> relProps) {
        this.relProps = relProps;
    }
    /**
     * Getter method for relProps
     */
    public Map<String, Object> getRelProps() {
        return this.relProps;
    }
}
