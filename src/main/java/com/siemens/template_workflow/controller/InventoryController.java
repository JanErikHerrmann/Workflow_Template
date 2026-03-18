package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Inventory;
import com.siemens.template_workflow.model.Supplier;
import com.siemens.template_workflow.repository.InventoryRepository;
import com.siemens.template_workflow.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryRepository repo;
    private final SupplierRepository supplierRepo;

    public InventoryController(InventoryRepository repo, SupplierRepository supplierRepo) {
        this.repo = repo; this.supplierRepo = supplierRepo;
    }

    @GetMapping
    public List<Inventory> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Inventory get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Inventory> create(@Valid @RequestBody Inventory in) {
        // validate supplier exists if provided
        if (in.getSupplier() != null && in.getSupplier().getId() != null) {
            Supplier s = supplierRepo.findById(in.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + in.getSupplier().getId()));
            in.setSupplier(s);
        }
        in.setCreatedAt(Instant.now());
        in.setUpdatedAt(Instant.now());
        Inventory saved = repo.save(in);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public Inventory update(@PathVariable Long id, @Valid @RequestBody Inventory incoming) {
        return repo.findById(id).map(i -> {
            i.setItemName(incoming.getItemName());
            i.setDescription(incoming.getDescription());
            i.setQuantity(incoming.getQuantity());
            i.setLocation(incoming.getLocation());
            if (incoming.getSupplier() != null && incoming.getSupplier().getId() != null) {
                Supplier s = supplierRepo.findById(incoming.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + incoming.getSupplier().getId()));
                i.setSupplier(s);
            } else {
                i.setSupplier(null);
            }
            i.setPricePerUnit(incoming.getPricePerUnit());
            i.setReorderLevel(incoming.getReorderLevel());
            i.setUpdatedAt(Instant.now());
            return repo.save(i);
        }).orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findById(id).map(i -> { repo.delete(i); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + id));
    }
}

