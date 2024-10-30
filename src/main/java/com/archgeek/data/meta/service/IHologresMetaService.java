package com.archgeek.data.meta.service;

import com.archgeek.data.meta.resp.CatalogResp;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-29 10:55
 */
public interface IHologresMetaService {

    List<CatalogResp> getCatalogList();

}
