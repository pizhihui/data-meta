package com.archgeek.data.meta.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author pizhihui
 * @date 2024-10-31 13:37
 */
@Data
public class MetaCatalogPO {

    private Long fCatalogId;
    private String fCatalogName;
    private String fCatalogComment;
    private String fType;
    private String fProvider;
    private String fProperties;

    private LocalDateTime fCreateTime;
    private LocalDateTime fModifyTime;

}
