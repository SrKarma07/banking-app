package com.banking.account.infrastructure.mapper;

import com.banking.account.application.dto.AccountCreateRequest;
import com.banking.account.application.dto.AccountResponse;
import com.banking.account.domain.model.Account;
import com.banking.common.mapper.MapstructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapstructConfig.class)
public interface AccountMapper {

    /* ---------- DTO → Entity ---------- */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type",
            expression = "java(Account.Type.valueOf(dto.getType()))")
    @Mapping(target = "status",
            expression = "java(Account.Status.valueOf(dto.getStatus()))")
    @Mapping(target = "currentBalance", source = "initialBalance")
    @Mapping(target = "customer", ignore = true)
    Account toEntity(AccountCreateRequest dto);

    /* ---------- Entity → DTO ---------- */
    @Mapping(target = "type",
            expression = "java(entity.getType().name())")
    @Mapping(target = "status",
            expression = "java(entity.getStatus().name())")
    AccountResponse toResponse(Account entity);
}
