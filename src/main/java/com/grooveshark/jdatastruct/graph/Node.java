package com.grooveshark.jdatastruct.graph;


import java.util.HashSet;

import org.apache.log4j.Logger;

public class Node<T extends Comparable<T>> implements Comparable<Node<T>>
{
    public static final Logger log = Logger.getLogger(Node.class);

    protected T value;
    protected HashSet<Node<T>> adjNodes = new HashSet<Node<T>>();

    protected double distance = Double.POSITIVE_INFINITY;

    public Node() {
    }

    public Node(T val) {
        this.value = val;
    }

    public int compareTo(Node<T> o) {
        return this.value.compareTo(o.value);
    }
}
