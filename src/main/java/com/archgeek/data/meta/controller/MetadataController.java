package com.archgeek.data.meta.controller;

import com.archgeek.data.meta.resp.CatalogResp;
import com.archgeek.data.meta.resp.Message;
import com.archgeek.data.meta.service.IHologresMetaService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-29 10:55
 */
@RestController("metadata")
public class MetadataController {

    @Autowired
    private IHologresMetaService hologresMetaService;

    @RequestMapping(path = "catalogs", method = RequestMethod.GET)
    public Message queryCatalogInfo() {
        try {
            List<CatalogResp> catalogList = hologresMetaService.getCatalogList();
            return Message.ok().setData(catalogList);
        } catch (Exception e) {
            return Message.error("请求 catalog 接口错误", e);
        }
    }

}
