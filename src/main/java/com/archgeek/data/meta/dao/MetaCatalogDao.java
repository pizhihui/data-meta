package com.archgeek.data.meta.dao;

import com.archgeek.data.meta.dao.po.MetaCatalogPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-31 13:36
 */
@Mapper
public interface MetaCatalogDao {

    @Select("select * from data_meta_db.t_meta_catalog;")
    List<MetaCatalogPO> selctCatalogList();

}
