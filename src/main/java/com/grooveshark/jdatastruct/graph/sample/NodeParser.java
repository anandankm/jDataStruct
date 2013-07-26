package com.grooveshark.jdatastruct.graph.sample;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.IOException;

import com.grooveshark.jdatastruct.graph.sample.entities.Location;
import com.grooveshark.jdatastruct.graph.sample.entities.GNode;
import com.grooveshark.jdatastruct.graph.sample.entities.GEdge;
import com.grooveshark.util.FileUtils;
import com.grooveshark.util.StringUtils;

import org.apache.log4j.Logger;

public class NodeParser
{

    public static final Logger log = Logger.getLogger(NodeParser.class);

    public List<GNode> nodes = new LinkedList<GNode>();
    public List<GEdge> edges = new LinkedList<GEdge>();
    public List<String> nodeFileLines;
    public List<String> locationLines;

    public HashMap<String, Integer> friendsToBase = new HashMap<String, Integer>();
    public HashMap<String, String> locationMap = new HashMap<String, String>();
    public GNode baseNode;

    public Location baseLocation;
    public Location closeLocation;
    public Location farLocation;

    public NodeParser(String nodeFile, String locationFile) throws IOException {
        this.locationLines = FileUtils.readFile(locationFile);
        this.nodeFileLines = FileUtils.readFile(nodeFile);
        this.parseLocationFile();
        this.parseNodes();
    }

    public void parseLocationFile() {
        for (String line : this.locationLines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] lineSplits = line.split("\t");
            String desc = lineSplits[0].trim();
            if (desc.equalsIgnoreCase("Base") ||
                desc.equalsIgnoreCase("Close") ||
                desc.equalsIgnoreCase("Far"))
            {
                Location location = new Location();
                location.city = lineSplits[1].trim();
                location.latitude = Float.parseFloat(lineSplits[2].trim());
                location.longitude = Float.parseFloat(lineSplits[3].trim());
                location.state = lineSplits[4].trim();
                location.country = lineSplits[5].trim();
                if (desc.equalsIgnoreCase("Base")) {
                    this.baseLocation = location;
                } else if (desc.equalsIgnoreCase("Close")) {
                    this.closeLocation = location;
                } else if (desc.equalsIgnoreCase("Far")) {
                    this.farLocation = location;
                }

            }
        }
    }

    public void displayNodes() {
        System.out.println("Nodes:");
        for (GNode node : this.nodes) {
            System.out.println(node.toString());
        }
    }

    public void displayEdges() {
        System.out.println("Edges:");
        for (GEdge edge : this.edges) {
            System.out.println(edge.toString());
        }
    }

    public void parseNodes() {
        for (String line : this.nodeFileLines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] splits = line.split("\t");
            splits[0] = splits[0].trim();
            if (StringUtils.isInteger(splits[0])) {
                int userid = Integer.parseInt(splits[0]);
                String username = splits[1].trim();
                GNode node = new GNode(userid, username);
                setFakeProperties(node);
                this.nodes.add(node);
                String[] usernameSplits = node.username.split(" ");
                int uslen = usernameSplits.length;
                if (uslen == 1) {
                    this.baseNode = node;
                    this.setLocation(node, this.baseLocation);
                } else if (uslen == 3) {
                    String type = usernameSplits[1].trim();
                    this.createEdges(type, this.baseNode.userid, userid);
                    this.friendsToBase.put(username, userid);
                } else if (uslen == 5) {
                    String friendsUname = usernameSplits[0] + " " +
                                          usernameSplits[1] + " " +
                                          usernameSplits[2];
                    if (!this.friendsToBase.isEmpty() && this.friendsToBase.containsKey(friendsUname)) {
                        int friendsUserid = this.friendsToBase.get(friendsUname);
                        this.createEdges(usernameSplits[3].trim(), friendsUserid, userid);
                    }
                }
                if (uslen > 1) {
                    if (usernameSplits[uslen-1].equalsIgnoreCase("Close")) {
                        this.setLocation(node, this.closeLocation);
                    } else if (usernameSplits[uslen-1].equalsIgnoreCase("Far")) {
                        this.setLocation(node, this.farLocation);
                    }
                }
            } else {
                continue;
            }
        }
    }

    public void setLocation(GNode node, Location location) {
        node.city = location.city;
        node.latitude = location.latitude;
        node.longitude = location.longitude;
        node.state = location.state;
        node.country = location.country;
    }

    public void createEdges(String type, int sUserid, int eUserid) {
        GEdge edge = null;
        if (type.equalsIgnoreCase("Mutual") || type.equalsIgnoreCase("Following")) {
            edge = new GEdge(sUserid, eUserid);
            this.edges.add(edge);
        }
        if (type.equalsIgnoreCase("Mutual") || type.equalsIgnoreCase("Follower")) {
            edge = new GEdge(eUserid, sUserid);
            this.edges.add(edge);
        }
    }

    public void setFakeProperties(GNode node) {
        node.fname =  node.userid + " First Name";
        node.lname =  node.userid + " Last Name";
        node.mname =  node.userid + " Middle Name";
        node.state =  node.userid + " State";
        node.country =  node.userid + " Country";
        node.zip =  node.userid + " zip";
        node.email = node.userid + "@grooveshark.com";
    }

}
