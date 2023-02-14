package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.TestDTO;
import com.example.demo.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author chenx
 * @since 2019-11-21
 */
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {


    IPage<TestEntity> findPage(Page<TestEntity> page, @Param("name") String name);

    IPage<TestDTO> findPageDTO(Page<TestEntity> page, @Param("name") String name);

    default IPage<TestEntity> findPageTest(Page<TestEntity> page, @Param("name") String name) {
        LambdaQueryWrapper<TestEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(TestEntity::getName, name);
        queryWrapper.orderByDesc(TestEntity::getCreateTime);
        return selectPage(page, queryWrapper);
    }

    @Select("SELECT * FROM pf_test WHERE name like '%${name}%'")
    IPage<TestDTO> selectPage1(Page<TestDTO> page, @Param("name") String name);

    @Select("SELECT * FROM pf_test WHERE id = '${id}'")
    TestDTO findById(@Param("id") String id);
}
