package wkallil.card.creditCardManager.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.dtos.CreateOwnerDTO;
import wkallil.card.creditCardManager.dtos.OwnerDTO;
import wkallil.card.creditCardManager.mappers.OwnerMapper;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.repositories.OwnerRepository;

import java.util.List;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    private final OwnerMapper ownerMapper;

    public OwnerService(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
        this.ownerRepository = ownerRepository;
        this.ownerMapper = ownerMapper;
    }

    @Transactional
    public OwnerDTO createOwner(CreateOwnerDTO dto) {
        if (ownerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Owner already exists");
        }

        Owner owner = ownerMapper.toEntity(dto);
        Owner savedOwner = ownerRepository.save(owner);
        return ownerMapper.toDto(savedOwner);
    }

    @Transactional(readOnly = true)
    public List<OwnerDTO> getAllOwners() {
        List<Owner> owners = ownerRepository.findAll();
        return ownerMapper.toDtoList(owners);
    }

    @Transactional(readOnly = true)
    public OwnerDTO getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        return ownerMapper.toDto(owner);
    }

    @Transactional(readOnly = true)
    public Owner getOwnerEntityById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
    }
}
