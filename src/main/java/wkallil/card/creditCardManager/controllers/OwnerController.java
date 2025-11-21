package wkallil.card.creditCardManager.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wkallil.card.creditCardManager.dtos.CreateOwnerDTO;
import wkallil.card.creditCardManager.dtos.OwnerDTO;
import wkallil.card.creditCardManager.services.OwnerService;

import java.util.List;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    public ResponseEntity<OwnerDTO> createOwner(@Valid @RequestBody CreateOwnerDTO dto) {
        OwnerDTO ownerDTO = ownerService.createOwner(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerDTO);
    }

    @GetMapping
    public ResponseEntity<List<OwnerDTO>> getAllOwners() {
        return ResponseEntity.ok(ownerService.getAllOwners());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerDTO> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }
}
