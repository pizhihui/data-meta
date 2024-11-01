package com.archgeek.data.meta.resp;

import lombok.Data;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-31
 */
@Data
public class MetaTableDetailResp {

    private String name;
    private String comment;

    private List<MetaColumnResp> columns;

    private List<Object> indexes;

    private List<Object> partitioning;

}
