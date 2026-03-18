package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Notification;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.repository.NotificationRepository;
import com.siemens.template_workflow.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationRepository repo;
    private final EmployeeRepository employeeRepo;

    public NotificationController(NotificationRepository repo, EmployeeRepository employeeRepo) {
        this.repo = repo; this.employeeRepo = employeeRepo;
    }

    @GetMapping
    public List<Notification> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Notification get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id)); }

    @PostMapping
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification n) {
        Employee e = employeeRepo.findById(n.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + n.getEmployee().getId()));
        n.setEmployee(e);
        n.setCreatedAt(Instant.now());
        if (n.getIsRead() == null) n.setIsRead(false);
        return ResponseEntity.ok(repo.save(n));
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable Long id, @Valid @RequestBody Notification incoming) {
        return repo.findById(id).map(n -> {
            if (incoming.getEmployee() != null && incoming.getEmployee().getId() != null) {
                Employee e = employeeRepo.findById(incoming.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getEmployee().getId()));
                n.setEmployee(e);
            }
            n.setType(incoming.getType());
            n.setMessage(incoming.getMessage());
            n.setIsRead(incoming.getIsRead());
            n.setCreatedAt(incoming.getCreatedAt());
            return repo.save(n);
        }).orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(n -> { repo.delete(n); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + id)); }
}

