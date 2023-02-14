package com.example.demo.convert;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.config.StructConfig;
import com.example.demo.dto.TestDTO;
import com.example.demo.entity.TestEntity;
import com.example.demo.request.TestReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author cx
 */
@Mapper(config = StructConfig.class)
public interface TestConvert {

    TestConvert TEST_CONVERT = Mappers.getMapper(TestConvert.class);

    TestEntity toEntity(TestReq req);

    Page<TestDTO> toPageDTO(IPage<TestEntity> page);

}
