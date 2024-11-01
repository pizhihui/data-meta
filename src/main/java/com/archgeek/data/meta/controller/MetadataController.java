package com.archgeek.data.meta.controller;

import com.archgeek.data.meta.resp.MetaCatalogResp;
import com.archgeek.data.meta.resp.Message;
import com.archgeek.data.meta.resp.MetaColumnResp;
import com.archgeek.data.meta.resp.MetaTableDetailResp;
import com.archgeek.data.meta.service.IHologresMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            List<MetaCatalogResp> catalogList = hologresMetaService.getCatalogList();
            return Message.ok().setMap("catalogs", catalogList);
        } catch (Exception e) {
            return Message.error("请求 catalog 接口错误", e);
        }
    }


    @RequestMapping(path = "/table/detail", method = RequestMethod.GET)
    public Message queryTableDetail() {

        MetaTableDetailResp tableDetailResp = new MetaTableDetailResp();
        tableDetailResp.setName("t_22222");
        tableDetailResp.setComment("t_2222 desc");

        MetaColumnResp co1 = new MetaColumnResp("id", "col1111", "long", false, false);
        MetaColumnResp co2 = new MetaColumnResp("name", "name2222", "string", true, false);
        MetaColumnResp co3 = new MetaColumnResp("age", "agexxxx", "long", false, false);
        List<MetaColumnResp> cols = Arrays.asList(co1, co2, co3);
        tableDetailResp.setColumns(cols);

        Map<String, Object> index1 = new HashMap<>();
        index1.put("name", "t_22222_pkey");
        index1.put("indexType", "PRIMARY_KEY");
        index1.put("fieldNames", Arrays.asList(Arrays.asList("id")));

        tableDetailResp.setIndexes(Arrays.asList(index1));

        return Message.ok().setMap("table", tableDetailResp);
    }

}
