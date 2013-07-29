package com.grooveshark.jdatastruct.graph.sample.entities;


import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;

public class GNode
{
    public int userid;

    public static final String USERID_KEY = "userid";
    public static final String USERNAME_KEY = "username";
    public static final String FNAME_KEY = "username";
    public static final String LNAME_KEY = "username";
    public static final String MNAME_KEY = "username";
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

    public GNode(int userid, String username) {
        this.userid = userid;
        this.username = username;
    }

    public static HashSet<String> getIndexKeys() {
        if (GNode.indexKeys == null) {
            GNode.indexKeys = new HashSet<String>();
            GNode.indexKeys.add("username");
            GNode.indexKeys.add("fname");
            GNode.indexKeys.add("lname");
            GNode.indexKeys.add("mname");
            GNode.indexKeys.add("city");
            GNode.indexKeys.add("state");
            GNode.indexKeys.add("country");
            GNode.indexKeys.add("email");
        }
        return GNode.indexKeys;
    }

    public Map<String, Object> getProps() {
        if (this.props.isEmpty()) {
            this.props.put("username", this.username);
            this.props.put("fname", this.fname);
            this.props.put("lname", this.fname);
            this.props.put("mname", this.mname);
            this.props.put("city", this.location.city);
            this.props.put("state", this.location.state);
            this.props.put("country", this.location.country);
            this.props.put("zip", this.location.zip);
            this.props.put("email", this.email);
            this.props.put("latitude", this.location.latitude);
            this.props.put("longitude", this.location.longitude);
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
