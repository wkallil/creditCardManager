package wkallil.card.creditCardManager.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wkallil.card.creditCardManager.dtos.CreateOwnerDTO;
import wkallil.card.creditCardManager.dtos.OwnerDTO;
import wkallil.card.creditCardManager.models.Owner;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    Owner toEntity(CreateOwnerDTO dto);

    OwnerDTO toDto(Owner entity);

    List<OwnerDTO> toDtoList(List<Owner> entitiesList);

    List<Owner> toEntityList(List<OwnerDTO> dtoList);
}
