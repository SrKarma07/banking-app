package com.banking.account.infrastructure.mapper;

import com.banking.account.application.dto.MovementResponse;
import com.banking.account.domain.model.Movement;
import com.banking.common.mapper.MapstructConfig;
import org.mapstruct.*;

@Mapper(config = MapstructConfig.class)
public interface MovementMapper {

    @Mapping(target = "type", expression = "java(entity.getType().name())")
    MovementResponse toResponse(Movement entity);
}
