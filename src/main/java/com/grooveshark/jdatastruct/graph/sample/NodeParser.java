package com.grooveshark.jdatastruct.graph.sample;

import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.io.IOException;

import com.grooveshark.util.FileUtils;
import com.grooveshark.util.StringUtils;

import org.apache.log4j.Logger;

public class NodeParser
{

    public static final Logger log = Logger.getLogger(NodeParser.class);

    public List<Node> nodes = new LinkedList<Node>();
    public List<Edge> edges = new LinkedList<Edge>();
    public List<String> nodeFileLines;

    public HashMap<String, Integer> friendsToBase = new HashMap<String, Integer>();
    public Node baseNode;

    public NodeParser(String nodeFile) throws IOException {
        this.nodeFileLines = FileUtils.readFile(nodeFile);
        this.parseNodes();
    }

    public void displayNodes() {
        System.out.println("Nodes:");
        for (Node node : this.nodes) {
            System.out.println(node.toString());
        }
    }


    public void displayEdges() {
        System.out.println("Edges:");
        for (Edge edge : this.edges) {
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
                Node node = new Node(userid, username);
                setFakeProperties(node);
                this.nodes.add(node);
                String[] usernameSplits = node.username.split(" ");
                if (usernameSplits.length == 1) {
                    this.baseNode = node;
                } else if (usernameSplits.length == 3) {
                    String type = usernameSplits[1].trim();
                    this.createEdges(type, this.baseNode.userid, userid);
                    this.friendsToBase.put(username, userid);
                } else if (usernameSplits.length == 5) {
                    String friendsUname = usernameSplits[0] + " " +
                                          usernameSplits[1] + " " +
                                          usernameSplits[2];
                    if (!this.friendsToBase.isEmpty() && this.friendsToBase.containsKey(friendsUname)) {
                        int friendsUserid = this.friendsToBase.get(friendsUname);
                        this.createEdges(usernameSplits[3].trim(), friendsUserid, userid);
                    }
                }
            } else {
                continue;
            }
        }
    }

    public void createEdges(String type, int sUserid, int eUserid) {
        Edge edge = null;
        if (type.equalsIgnoreCase("Mutual") || type.equalsIgnoreCase("Following")) {
            edge = new Edge(sUserid, eUserid);
            this.edges.add(edge);
        }
        if (type.equalsIgnoreCase("Mutual") || type.equalsIgnoreCase("Follower")) {
            edge = new Edge(eUserid, sUserid);
            this.edges.add(edge);
        }
    }

    public void setFakeProperties(Node node) {
        node.fname =  node.userid + " First Name";
        node.lname =  node.userid + " Last Name";
        node.mname =  node.userid + " Middle Name";
        node.city =  node.userid + " City";
        node.state =  node.userid + " State";
        node.country =  node.userid + " Country";
        node.zip =  node.userid + " zip";
        node.email = node.userid + "@grooveshark.com";
    }

}
