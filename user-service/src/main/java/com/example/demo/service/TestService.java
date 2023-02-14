package com.example.demo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.TestDTO;
import com.example.demo.entity.TestEntity;
import com.example.demo.mapper.TestMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.convert.TestConvert.TEST_CONVERT;

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
public class TestService extends ServiceImpl<TestMapper, TestEntity> {
    private final TestMapper testMapper;

    public IPage<TestEntity> findPage(String name, Page<TestEntity> page) {
        return testMapper.findPage(page, name);
    }

    public IPage<TestDTO> findPageDTO(String name, Page<TestEntity> page) {
        return testMapper.findPageDTO(page, name);
    }

    public TestDTO findById(String id) {
        return testMapper.findById(id);
    }

    public Page<TestDTO> selectPage(String name, Page<TestEntity> page) {
        IPage<TestEntity> result = testMapper.findPageTest(page, name);
        return TEST_CONVERT.toPageDTO(result);
    }

    public IPage<TestDTO> selectPage1(String name, Page<TestDTO> page) {
        return testMapper.selectPage1(page, name);
    }
}
