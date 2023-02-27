
package com.example.demo.repository;

import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.TestTable;
import com.example.demo.request.QueryUserReq;
import com.huagui.service.dto.Page;
import com.huagui.service.repository.BaseRep;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TestRep {


    @Autowired
    DSLContext dsl;

    @Autowired
    BaseRep baseRep;

    TestTable test = TestTable.PF_TEST.as("test");

    public Page<TestDTO> findPageByName(QueryUserReq req) {
        Condition condition = test.NAME.like("%" + req.getName() + "%");
        return baseRep.page(TestDTO.class, test, test.fields(), condition, List.of(test.CREATED_DT.desc()), req);
    }

}

