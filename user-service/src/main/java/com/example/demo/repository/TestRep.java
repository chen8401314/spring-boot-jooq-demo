
package com.example.demo.repository;

import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.TestTable;
import com.example.demo.request.QueryTestReq;
import com.common.service.dto.PageDTO;
import com.common.service.repository.BaseRep;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TestRep {

    private final BaseRep baseRep;

    TestTable test = TestTable.PF_TEST.as("test");

    public PageDTO<TestDTO> findPageByName(QueryTestReq req) {
        Condition condition = test.NAME.like("%" + req.getName() + "%");
        return baseRep.page(TestDTO.class, test, test.fields(), condition, List.of(test.CREATED_DT.desc()), req);
    }

}

