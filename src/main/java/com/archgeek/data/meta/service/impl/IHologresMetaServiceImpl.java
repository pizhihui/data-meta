package com.archgeek.data.meta.service.impl;

import com.archgeek.data.meta.dao.MetaCatalogDao;
import com.archgeek.data.meta.dao.po.MetaCatalogPO;
import com.archgeek.data.meta.resp.MetaCatalogResp;
import com.archgeek.data.meta.resp.MetaColumnResp;
import com.archgeek.data.meta.resp.MetaSchemaResp;
import com.archgeek.data.meta.resp.MetaTableResp;
import com.archgeek.data.meta.service.IHologresMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-29 10:55
 */
@Service
public class IHologresMetaServiceImpl implements IHologresMetaService {

    @Autowired
    private MetaCatalogDao metaCatalogDao;

    @Override
    public List<MetaCatalogResp> getCatalogList() {
        List<MetaCatalogResp> list = new ArrayList<>();

        List<MetaCatalogPO> metaCatalogPOS = metaCatalogDao.selctCatalogList();
        for (MetaCatalogPO po : metaCatalogPOS) {
            MetaCatalogResp catalogResp = new MetaCatalogResp();
            catalogResp.setName(po.getFCatalogName());
            catalogResp.setComment(po.getFCatalogComment());
            catalogResp.setProvider(po.getFProvider());
            catalogResp.setType(po.getFType());
            catalogResp.setProperties(po.getFProperties());
            list.add(catalogResp);
        }

        return list;
    }

    @Override
    public List<MetaSchemaResp> getSchemaList() {
        return null;
    }

    @Override
    public List<MetaTableResp> getTableList() {
        return null;
    }

    @Override
    public List<MetaColumnResp> getColumnList() {
        return null;
    }
}
