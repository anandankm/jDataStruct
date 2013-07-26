package com.grooveshark.jdatastruct.graph.sample;


public class Node
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

    public Node(int userid, String username) {
        this.userid = userid;
        this.username = username;
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
