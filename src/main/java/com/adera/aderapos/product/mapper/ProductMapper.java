package com.adera.aderapos.product.mapper;

import com.adera.aderapos.product.dtos.InventoryDTO;
import com.adera.aderapos.product.dtos.ProductDTO;
import com.adera.aderapos.product.entities.Inventory;
import com.adera.aderapos.product.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper interface for converting between Product and ProductDTO,
 * as well as Inventory and InventoryDTO.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    ProductDTO toDto(Product product);

    @Mapping(source = "shopId", target = "shop.id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "deletedAt", target = "deletedAt")
    @Mapping(source = "deletedBy", target = "deletedBy")
    Product toEntity(ProductDTO dto);

    InventoryDTO toDto(Inventory inventory);

    Inventory toEntity(InventoryDTO dto);

    List<ProductDTO> toProductDtoList(List<Product> products);

    List<InventoryDTO> toInventoryDtoList(List<Inventory> inventories);
}
