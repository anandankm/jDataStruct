package com.grooveshark.jdatastruct.graph;


import java.util.HashSet;

import org.apache.log4j.Logger;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>>
{
    public static final Logger log = Logger.getLogger(Node.class);

    protected T value;
    protected HashSet<Node<T>> adjNodes = new HashSet<Node<T>>();

    protected double distance = Double.POSITIVE_INFINITY;
    protected boolean visited;
    protected Node<T> predecessor = null;

    public Node() {
    }

    public Node(T val) {
        this.value = val;
    }

    public int compareTo(Node<T> o) {
        return this.value.compareTo(o.value);
    }

    public boolean equals(Node<T> o) {
        return this.value.equals(o.value);
    }

    public String toString() {
        return "{adjNodesSize:" + this.adjNodes.size() + "}";
    }

    public boolean addToAdj(Node o) {
        return this.adjNodes.add(o);
    }

    /**
     * Setter method for distance
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }
    /**
     * Getter method for distance
     */
    public double getDistance() {
        return this.distance;
    }
    /**
     * Setter method for visited
     */
    public void setVisited(boolean visited) {
        this.visited = visited;
    }
    /**
     * Getter method for visited
     */
    public boolean getVisited() {
        return this.visited;
    }
    /**
     * Setter method for value
     */
    public void setValue(T value) {
        this.value = value;
    }
    /**
     * Getter method for value
     */
    public T getValue() {
        return this.value;
    }
    /**
     * Setter method for adjNodes
     */
    public void setAdjNodes(HashSet<Node<T>> adjNodes) {
        this.adjNodes = adjNodes;
    }
    /**
     * Getter method for adjNodes
     */
    public HashSet<Node<T>> getAdjNodes() {
        return this.adjNodes;
    }
    /**
     * Setter method for predecessor
     */
    public void setPredecessor(Node<T> predecessor) {
        this.predecessor = predecessor;
    }
    /**
     * Getter method for predecessor
     */
    public Node<T> getPredecessor() {
        return this.predecessor;
    }
}
