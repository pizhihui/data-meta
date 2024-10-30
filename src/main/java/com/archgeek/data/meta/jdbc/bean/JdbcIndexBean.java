package com.archgeek.data.meta.jdbc.bean;

import java.util.Arrays;

/**
 * @author pizhihui
 * @date 2024-10-28 21:56
 */
public class JdbcIndexBean {

    private String colName;
    private int order;

    // primary key  [[id]]
    // key   [[name],[age]]
    private IndexType indexType;
    String[][] fieldNames;
    private String idxName;

    // 中间
    public JdbcIndexBean(IndexType indexType, String colName, String pkName, int order) {
        this.indexType = indexType;
        this.colName = colName;
        this.idxName = pkName;
        this.order = order;
    }

    // 结果
    public JdbcIndexBean(IndexType indexType, String idxName, String[][] fieldNames) {
        this.indexType = indexType;
        this.idxName = idxName;
        this.fieldNames = fieldNames;
    }


    public enum IndexType {
        PRIMARY_KEY,
        UNIQUE_KEY
    }


    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getIdxName() {
        return idxName;
    }

    public void setIdxName(String idxName) {
        this.idxName = idxName;
    }

    public IndexType getIndexType() {
        return indexType;
    }

    public void setIndexType(IndexType indexType) {
        this.indexType = indexType;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String[][] getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(String[][] fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public String toString() {

        StringBuilder res = new StringBuilder();
        for (String[] fieldName : fieldNames) {
            res.append(Arrays.toString(fieldName)).append("\t");
        }

        return "JdbcIndexBean{" +
                "colName='" + colName + '\'' +
                ", idxName='" + idxName + '\'' +
                ", indexType=" + indexType +
                ", order=" + order +
                ", fieldNames=" + res +
                '}';
    }
}
