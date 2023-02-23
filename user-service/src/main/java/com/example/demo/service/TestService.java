package com.example.demo.service;

import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.daos.TestDao;
import com.example.demo.jooq.tables.pojos.TestEntity;
import com.example.demo.jooq.tables.records.TestRecord;
import com.example.demo.repository.TestRep;
import com.example.demo.request.QueryUserReq;
import com.huagui.service.dto.Page;
import com.huagui.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenx
 * @since 2019-11-21
 */
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TestService extends ServiceImpl<TestDao, TestEntity, TestRecord> {
    TestRep testRep;

    public Page<TestDTO> findPageByName(QueryUserReq req) {
        return testRep.findPageByName(req);
    }
}
