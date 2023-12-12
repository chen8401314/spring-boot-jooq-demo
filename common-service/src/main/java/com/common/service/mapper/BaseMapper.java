package com.common.service.mapper;

import com.common.service.config.InsertFill;
import com.common.service.config.UpdateFill;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;

import java.util.Collection;
import java.util.List;

/**
 * @author lijunsong
 * @className BaseMapper
 * @description 基础转换类
 * @date 2020-06-05 11:31
 **/
@MapperConfig
public interface BaseMapper<E, R, T> {
    @InheritConfiguration
    T toDTO(E entity);

    @InheritConfiguration
    List<T> toDTOList(Collection<E> entities);

    @InheritInverseConfiguration
    @InsertFill
    E toEntity(R req);

    @UpdateFill
    @InheritInverseConfiguration
    void copy(R req, @MappingTarget E entiy);

    @InheritInverseConfiguration
    List<E> toEntities(Collection<R> reqs);
}
