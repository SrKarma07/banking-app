package com.banking.customer.infrastructure.mapper;

import com.banking.common.mapper.MapstructConfig;
import com.banking.customer.application.dto.PersonDTO;
import com.banking.customer.domain.model.Person;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface PersonMapper {
    Person toEntity(PersonDTO dto);
    PersonDTO toDTO(Person entity);
}
