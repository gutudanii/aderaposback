package com.adera.aderapos.sales.mapper;

import com.adera.aderapos.sales.dtos.SaleDTO;
import com.adera.aderapos.sales.dtos.SaleItemDTO;
import com.adera.aderapos.sales.entities.Sale;
import com.adera.aderapos.sales.entities.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    SaleDTO toDto(Sale sale);

    @Mapping(source = "shopId", target = "shop.id")
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    Sale toEntity(SaleDTO dto);

    SaleItemDTO toDto(SaleItem item);

    SaleItem toEntity(SaleItemDTO dto);

    List<SaleItemDTO> toDtoList(List<SaleItem> items);

    List<SaleItem> toEntityList(List<SaleItemDTO> dtos);
}
