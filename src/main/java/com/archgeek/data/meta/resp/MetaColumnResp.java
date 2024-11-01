package com.archgeek.data.meta.resp;

import lombok.Data;

/**
 * @author pizhihui
 * @date 2024-10-31 15:25
 */
@Data
public class MetaColumnResp {

    private Long id;
    private String name;
    private String comment;
    private String type;
    private Boolean nullable;
    private Boolean autoIncrement;

    public MetaColumnResp() {
    }

    public MetaColumnResp(String name, String comment, String type, Boolean nullable, Boolean autoIncrement) {
        this.name = name;
        this.comment = comment;
        this.type = type;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
    }


    public static class DefaultValue {
        private String dataType;
        private String type;
        private String value;
    }

}
