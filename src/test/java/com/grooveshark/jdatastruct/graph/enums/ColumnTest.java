package com.grooveshark.jdatastruct.graph.enums;


import org.junit.Test;
import static org.junit.Assert.fail;

public class ColumnTest
{
    @Test
    public void testColumn() {
        try {
            System.out.println("UserID num: " + Column.userid.num);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to test column: " + ex.getMessage());
        }
    }

}
