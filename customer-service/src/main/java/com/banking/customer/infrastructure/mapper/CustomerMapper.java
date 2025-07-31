package com.banking.customer.infrastructure.mapper;

import com.banking.common.mapper.MapstructConfig;
import com.banking.customer.application.dto.CustomerResponse;
import com.banking.customer.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapstructConfig.class, uses = { PersonMapper.class })
public interface CustomerMapper {

    @Mapping(target = "status", expression = "java(customer.getStatus().name())")
    @Mapping(target = "person", source = "person")
    CustomerResponse toResponse(Customer customer);
}
