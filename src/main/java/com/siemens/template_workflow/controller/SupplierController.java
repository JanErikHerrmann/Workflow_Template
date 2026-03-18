package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Supplier;
import com.siemens.template_workflow.repository.SupplierRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierRepository repo;

    public SupplierController(SupplierRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Supplier> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Supplier get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id)); }

    @PostMapping
    public ResponseEntity<Supplier> create(@Valid @RequestBody Supplier s) {
        s.setCreatedAt(Instant.now()); s.setUpdatedAt(Instant.now()); return ResponseEntity.ok(repo.save(s));
    }

    @PutMapping("/{id}")
    public Supplier update(@PathVariable Long id, @Valid @RequestBody Supplier incoming) {
        return repo.findById(id).map(s -> { s.setName(incoming.getName()); s.setContactEmail(incoming.getContactEmail()); s.setPhone(incoming.getPhone()); s.setAddress(incoming.getAddress()); s.setLocation(incoming.getLocation()); s.setUpdatedAt(Instant.now()); return repo.save(s); }).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(s -> { repo.delete(s); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id)); }
}

