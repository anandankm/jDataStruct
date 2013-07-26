package com.grooveshark.jdatastruct.graph.sample.entities;


import java.util.Map;
import java.util.HashMap;

public class GNode
{
    public int userid;
    public String username = "";
    public String fname = "";
    public String lname = "";
    public String mname = "";
    public String city = "";
    public String state = "";
    public String country = "";
    public String zip = "";
    public String email = "";
    public Float latitude;
    public Float longitude;
    public Map<String, Object> props = new HashMap<String, Object>();
    public String indexText = "";

    public GNode(int userid, String username) {
        this.userid = userid;
        this.username = username;
    }

    public Map<String, Object> getProps() {
        if (this.props.isEmpty()) {
            this.props.put("username", this.username);
            this.props.put("fname", this.fname);
            this.props.put("lname", this.fname);
            this.props.put("mname", this.mname);
            this.props.put("city", this.city);
            this.props.put("state", this.state);
            this.props.put("country", this.country);
            this.props.put("zip", this.zip);
            this.props.put("email", this.email);
            this.props.put("latitude", this.latitude);
            this.props.put("longitude", this.longitude);
        }
        return this.props;
    }

    public String getIndexText() {
        if (this.indexText.isEmpty()) {
            StringBuilder fullText = new StringBuilder();
            fullText.append(this.username);
            fullText.append(this.fname);
            fullText.append(this.fname);
            fullText.append(this.mname);
            fullText.append(this.city);
            fullText.append(this.state);
            fullText.append(this.country);
            fullText.append(this.email);
            this.indexText = fullText.toString();
        }
        return this.indexText;
    }

    public String toString() {
        return  "[" + this.userid + "," +
                      this.username + "," +
                      this.fname + "," +
                      this.lname + "," +
                      this.mname + "," +
                      this.city + "," +
                      this.state + "," +
                      this.country + "," +
                      this.zip + "," +
                      this.email + "," +
                      this.latitude + "," +
                      this.longitude + "]";
    }
}
