package com.grooveshark.jdatastruct.graph;

import org.neo4j.rest.graphdb.RestAPIFacade;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.Relationship;

import java.util.Iterator;

import static org.neo4j.helpers.collection.MapUtil.map;

public class RestNeo4j
{

    public static final Logger log = Logger.getLogger(RestNeo4j.class);

    private static final String SERVER_ROOT_URI = "http://localhost:7474/db/data";
    private RestAPI restAPI = null;

    private String url = null;

    public RestNeo4j() {
        this.restAPI = new RestAPIFacade(SERVER_ROOT_URI);
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

    public void checkIndex() {
    }

    public void clearDB() {
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
