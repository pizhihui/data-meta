package com.archgeek.data.meta.jdbc.bean;

/**
 * @author pizhihui
 * @date 2024-10-28 21:50
 */
public class JdbcColumnBean {

    private String colName;
    private String colComment;
    private String colType;
    private boolean nullable;
    private String colDefaultValue;

    public JdbcColumnBean(String colName, String colComment, String colType, boolean nullable, String colDefaultValue) {
        this.colName = colName;
        this.colComment = colComment;
        this.colType = colType;
        this.nullable = nullable;
        this.colDefaultValue = colDefaultValue;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public String getColComment() {
        return colComment;
    }

    public void setColComment(String colComment) {
        this.colComment = colComment;
    }

    public String getColType() {
        return colType;
    }

    public void setColType(String colType) {
        this.colType = colType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getColDefaultValue() {
        return colDefaultValue;
    }

    public void setColDefaultValue(String colDefaultValue) {
        this.colDefaultValue = colDefaultValue;
    }
}
