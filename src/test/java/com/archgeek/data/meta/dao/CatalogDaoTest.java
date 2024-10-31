package com.archgeek.data.meta.dao;

import com.archgeek.data.meta.dao.po.MetaCatalogPO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

/**
 * @author pizhihui
 * @date 2024-10-31 14:25
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CatalogDaoTest {

    @Autowired
    private MetaCatalogDao catalogDao;

    @Test
    @DisplayName("catalog list")
    void selctCatalogList() {

        List<MetaCatalogPO> jdbcCatalogs = catalogDao.selctCatalogList();
        System.out.println(jdbcCatalogs);

    }
}
