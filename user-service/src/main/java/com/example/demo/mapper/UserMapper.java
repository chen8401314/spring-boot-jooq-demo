package com.example.demo.mapper;


import com.example.demo.dto.UserDTO;
import com.example.demo.jooq.tables.pojos.UserEntity;
import com.huagui.service.config.StructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author cx
 */
@Mapper(config = StructConfig.class)
public interface UserMapper {

    UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(UserEntity entity);

}
