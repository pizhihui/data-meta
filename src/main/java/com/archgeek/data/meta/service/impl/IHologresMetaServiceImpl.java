package com.archgeek.data.meta.service.impl;

import com.archgeek.data.meta.resp.CatalogResp;
import com.archgeek.data.meta.service.IHologresMetaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-29 10:55
 */
@Service
public class IHologresMetaServiceImpl implements IHologresMetaService {
    @Override
    public List<CatalogResp> getCatalogList() {
        List<CatalogResp> list = new ArrayList<>();

        CatalogResp catalogResp = new CatalogResp();
        catalogResp.setName("holo01");
        catalogResp.setComment("holo01 comment");
        catalogResp.setProvider("jdbc-postgresql");
        catalogResp.setType("RELATIONAL");

        list.add(catalogResp);
        return list;
    }
}
