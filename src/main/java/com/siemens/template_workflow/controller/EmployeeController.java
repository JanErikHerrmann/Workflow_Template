package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Employee> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee e) {
        e.setCreatedAt(Instant.now());
        e.setUpdatedAt(Instant.now());
        Employee saved = repo.save(e);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @Valid @RequestBody Employee incoming) {
        return repo.findById(id).map(e -> {
            e.setUsername(incoming.getUsername());
            e.setEmail(incoming.getEmail());
            e.setRole(incoming.getRole());
            e.setPasswordHash(incoming.getPasswordHash());
            e.setUpdatedAt(Instant.now());
            return repo.save(e);
        }).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findById(id).map(e -> {
            repo.delete(e);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }
}

