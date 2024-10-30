package com.archgeek.data.meta.controller;

import com.archgeek.data.meta.resp.Message;
import com.archgeek.data.meta.service.IHologresMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
            return Message.ok().setData("hello");
        } catch (Exception e) {
            return Message.error("请求 catalog 接口错误", e);
        }
    }

}
