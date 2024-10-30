package com.archgeek.data.meta.jdbc.bean;

/**
 * @author pizhihui
 * @date 2024-10-28 21:30
 */
public class JdbcSchemaBean {

    private String schemaName;
    private String schemaComment;

    public JdbcSchemaBean(String schemaName, String schemaComment) {
        this.schemaName = schemaName;
        this.schemaComment = schemaComment;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaComment() {
        return schemaComment;
    }

    public void setSchemaComment(String schemaComment) {
        this.schemaComment = schemaComment;
    }

    @Override
    public String toString() {
        return "JdbcSchemaBean{" +
                "schemaName='" + schemaName + '\'' +
                ", schemaComment='" + schemaComment + '\'' +
                '}';
    }
}
