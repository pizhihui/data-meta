package com.archgeek.data.meta.resp;

import lombok.Data;

import java.util.Map;

/**
 * @author pizhihui
 * @date 2024-10-30
 */
@Data
public class CatalogResp {


    private String name;
    private String comment;
    private String provider;
    private String type;
    private Map<String, String> properties;

}
