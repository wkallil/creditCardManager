package wkallil.card.creditCardManager.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wkallil.card.creditCardManager.dtos.MonthlyChargeDTO;
import wkallil.card.creditCardManager.models.MonthlyCharge;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MonthlyChargeMapper {

    @Mapping(source = "expense.name", target = "expenseName")
    @Mapping(source = "expense.installments", target = "totalInstallments")
    @Mapping(source = "expense.recurring", target = "recurring")
    @Mapping(source = "expense.owner.name", target = "ownerName")
    MonthlyChargeDTO toDto(MonthlyCharge entity);

    List<MonthlyChargeDTO> toDtoList(List<MonthlyCharge> entitiesList);
}
