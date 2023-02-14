package com.example.demo.convert;


import com.example.demo.config.StructConfig;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.TestEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.request.TestReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author cx
 */
@Mapper(config = StructConfig.class)
public interface UserConvert {

    UserConvert USER_CONVERT = Mappers.getMapper(UserConvert.class);

    UserDTO toDTO(UserEntity entity);

}
