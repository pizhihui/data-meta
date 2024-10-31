package com.archgeek.data.meta.service;

import com.archgeek.data.meta.resp.MetaCatalogResp;
import com.archgeek.data.meta.resp.MetaColumnResp;
import com.archgeek.data.meta.resp.MetaSchemaResp;
import com.archgeek.data.meta.resp.MetaTableResp;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-29 10:55
 */
public interface IHologresMetaService {

    List<MetaCatalogResp> getCatalogList();

    List<MetaSchemaResp> getSchemaList();

    List<MetaTableResp> getTableList();

    List<MetaColumnResp> getColumnList();
}
