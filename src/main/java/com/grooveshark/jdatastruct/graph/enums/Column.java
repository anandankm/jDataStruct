package com.grooveshark.jdatastruct.graph.enums;

public enum Column {
    userid (0, Integer.class),
    username (1, String.class),
    fname (3, String.class),
    lname (4, String.class),
    mname (5, String.class);

    public int num;
    public Class<?> type;

    Column(int num, Class<?> type) {
        this.num = num;
    }
}
