package com.grooveshark.jdatastruct.graph.sample.entities;


import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import org.neo4j.graphdb.Node;

public class GNode
{
    public Long userid;

    public static final String NODE_INDEX = "users";

    public static final String USERID_KEY = "userid";
    public static final String USERNAME_KEY = "username";
    public static final String FNAME_KEY = "fname";
    public static final String LNAME_KEY = "lname";
    public static final String MNAME_KEY = "mname";
    public static final String CITY_KEY = "city";
    public static final String STATE_KEY = "state";
    public static final String ZIP_KEY = "zip";
    public static final String COUNTRY_KEY = "country";
    public static final String EMAIL_KEY = "email";
    public static final String LAT_KEY = "latitude";
    public static final String LON_KEY = "longitude";

    public String username = "";
    public String fname = "";
    public String lname = "";
    public String mname = "";
    public Location location;
    public String email = "";
    public Map<String, Object> props = new HashMap<String, Object>();
    public static HashSet<String> indexKeys = null;

    public GNode(long userid, String username) {
        this.userid = userid;
        this.username = username;
    }

    public GNode(Node node) {
        this.userid = node.getId();
        this.username = (String) node.getProperty(GNode.USERNAME_KEY);
        this.fname = (String) node.getProperty(GNode.FNAME_KEY);
        this.lname = (String) node.getProperty(GNode.LNAME_KEY);
        this.mname = (String) node.getProperty(GNode.MNAME_KEY);
        this.email = (String) node.getProperty(GNode.EMAIL_KEY);
        this.location = new Location();
        this.location.city = (String) node.getProperty(GNode.CITY_KEY);
        this.location.country = (String) node.getProperty(GNode.COUNTRY_KEY);
        this.location.state = (String) node.getProperty(GNode.STATE_KEY);
        this.location.zip = (String) node.getProperty(GNode.ZIP_KEY);
        Double lat = (Double) node.getProperty(GNode.LAT_KEY);
        Double lon = (Double) node.getProperty(GNode.LON_KEY);
        this.location.latitude = lat.floatValue();
        this.location.longitude = lon.floatValue();
    }

    public static HashSet<String> getIndexKeys() {
        if (GNode.indexKeys == null) {
            GNode.indexKeys = new HashSet<String>();
            GNode.indexKeys.add(GNode.USERNAME_KEY);
            GNode.indexKeys.add(GNode.FNAME_KEY);
            GNode.indexKeys.add(GNode.LNAME_KEY);
            GNode.indexKeys.add(GNode.MNAME_KEY);
            GNode.indexKeys.add(GNode.CITY_KEY);
            GNode.indexKeys.add(GNode.STATE_KEY);
            GNode.indexKeys.add(GNode.COUNTRY_KEY);
            GNode.indexKeys.add(GNode.EMAIL_KEY);
        }
        return GNode.indexKeys;
    }

    public Map<String, Object> getProps() {
        if (this.props.isEmpty()) {
            this.props.put(GNode.USERNAME_KEY, this.username);
            this.props.put(GNode.FNAME_KEY, this.fname);
            this.props.put(GNode.LNAME_KEY, this.fname);
            this.props.put(GNode.MNAME_KEY, this.mname);
            this.props.put(GNode.CITY_KEY, this.location.city);
            this.props.put(GNode.STATE_KEY, this.location.state);
            this.props.put(GNode.COUNTRY_KEY, this.location.country);
            this.props.put(GNode.ZIP_KEY, this.location.zip);
            this.props.put(GNode.EMAIL_KEY, this.email);
            this.props.put(GNode.LAT_KEY, this.location.latitude);
            this.props.put(GNode.LON_KEY, this.location.longitude);
        }
        return this.props;
    }

    public String toString() {
        return  "[" + this.userid + "," +
                      this.username + "," +
                      this.fname + "," +
                      this.lname + "," +
                      this.mname + "," +
                      this.location.city + "," +
                      this.location.state + "," +
                      this.location.country + "," +
                      this.location.zip + "," +
                      this.email + "," +
                      this.location.latitude + "," +
                      this.location.longitude + "]";
    }
}
