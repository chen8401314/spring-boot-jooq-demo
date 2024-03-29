package com.example.demo.service;

import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.daos.TestDao;
import com.example.demo.jooq.tables.pojos.TestEntity;
import com.example.demo.jooq.tables.records.TestRecord;
import com.example.demo.repository.TestRep;
import com.example.demo.request.QueryTestReq;
import com.common.service.dto.PageDTO;
import com.common.service.impl.ServiceImpl;
import com.example.demo.request.TestReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.mapper.TestMapper.TEST_MAPPER;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chenx
 * @since 2019-11-21
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class TestService extends ServiceImpl<TestDao, TestEntity, TestRecord> {
    private final TestRep testRep;

    public PageDTO<TestDTO> findPageByName(QueryTestReq req) {
        return testRep.findPageByName(req);
    }


}
