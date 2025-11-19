package wkallil.card.creditCardManager.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wkallil.card.creditCardManager.dtos.CreateExpenseDTO;
import wkallil.card.creditCardManager.dtos.ExpenseDTO;
import wkallil.card.creditCardManager.models.Expense;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "monthlyCharges", ignore = true)
    Expense toEntity(CreateExpenseDTO dto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "owner.name", target = "ownerName")
    ExpenseDTO toDto(Expense entity);

    List<ExpenseDTO> toDtoList(List<Expense> entitiesList);
}
