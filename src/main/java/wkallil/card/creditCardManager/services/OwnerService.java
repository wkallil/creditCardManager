package wkallil.card.creditCardManager.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wkallil.card.creditCardManager.controllers.OwnerController;
import wkallil.card.creditCardManager.dtos.CreateOwnerDTO;
import wkallil.card.creditCardManager.dtos.OwnerDTO;
import wkallil.card.creditCardManager.exceptions.OwnerAlreadyExistsException;
import wkallil.card.creditCardManager.exceptions.OwnerNotFoundException;
import wkallil.card.creditCardManager.mappers.OwnerMapper;
import wkallil.card.creditCardManager.models.Owner;
import wkallil.card.creditCardManager.repositories.OwnerRepository;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
            throw new OwnerAlreadyExistsException("Owner already exists");
        }

        Owner owner = ownerMapper.toEntity(dto);
        Owner savedOwner = ownerRepository.save(owner);
        OwnerDTO dtoWithHateoas = ownerMapper.toDto(savedOwner);
        CreateOwnerDTO createOwnerDTO = new CreateOwnerDTO();
        createOwnerDTO.setName(dto.getName());
        createOwnerDTO.setEmail(dto.getEmail());
        addHateoasCreateLinks(createOwnerDTO);
        addHateoasOwnerLinks(dtoWithHateoas);
        return dtoWithHateoas;
    }

    @Transactional(readOnly = true)
    public List<OwnerDTO> getAllOwners() {
        List<Owner> owners = ownerRepository.findAll();
        List<OwnerDTO> ownerDTOs = ownerMapper.toDtoList(owners);
        for (OwnerDTO dto : ownerDTOs) {
            addHateoasOwnerLinks(dto);
        }
        return ownerDTOs;
    }

    @Transactional(readOnly = true)
    public OwnerDTO getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found"));
        return ownerMapper.toDto(owner);
    }

    @Transactional(readOnly = true)
    public Owner getOwnerEntityById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new OwnerNotFoundException("Owner not found"));
    }

    private void addHateoasCreateLinks(CreateOwnerDTO dto) {
        dto.add(linkTo(methodOn(OwnerController.class).createOwner(dto)).withRel("create-owner").withType("POST"));
    }

    private void addHateoasOwnerLinks(OwnerDTO dto) {
        dto.add(linkTo(methodOn(OwnerController.class).getOwnerById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(OwnerController.class).getAllOwners()).withRel("GetAllOwners").withType("GET"));
    }
}

