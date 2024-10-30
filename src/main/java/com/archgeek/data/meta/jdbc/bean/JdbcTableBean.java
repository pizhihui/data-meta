package com.archgeek.data.meta.jdbc.bean;

/**
 * @author pizhihui
 * @date 2024-10-28 21:32
 */
public class JdbcTableBean {

    private String tableName;
    private String tableComment;

    public JdbcTableBean(String tableName, String tableComment) {
        this.tableName = tableName;
        this.tableComment = tableComment;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    @Override
    public String toString() {
        return "JdbcTableBean{" +
                "tableName='" + tableName + '\'' +
                ", tableComment='" + tableComment + '\'' +
                '}';
    }
}
