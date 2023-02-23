package com.example.demo.mapper;


import com.example.demo.config.StructConfig;
import com.example.demo.dto.TestDTO;
import com.example.demo.jooq.tables.pojos.TestEntity;
import com.example.demo.request.TestReq;
import com.huagui.service.config.InsertFill;
import com.huagui.service.config.UpdateFill;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * @author cx
 */
@Mapper(config = StructConfig.class)
public interface TestMapper {

    TestMapper TEST_MAPPER = Mappers.getMapper(TestMapper.class);

    TestDTO toDTO(TestEntity bean);

    @InsertFill
    TestEntity toEntity(TestReq bean);

    @UpdateFill
    void copy(TestReq req, @MappingTarget TestEntity testEntity);


}
