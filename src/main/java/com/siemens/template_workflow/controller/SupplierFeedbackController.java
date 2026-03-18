package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.SupplierFeedback;
import com.siemens.template_workflow.model.Supplier;
import com.siemens.template_workflow.model.Order;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.repository.SupplierFeedbackRepository;
import com.siemens.template_workflow.repository.SupplierRepository;
import com.siemens.template_workflow.repository.OrderRepository;
import com.siemens.template_workflow.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/supplier-feedback")
public class SupplierFeedbackController {
    private final SupplierFeedbackRepository repo;
    private final SupplierRepository supplierRepo;
    private final OrderRepository orderRepo;
    private final EmployeeRepository employeeRepo;

    public SupplierFeedbackController(SupplierFeedbackRepository repo, SupplierRepository supplierRepo, OrderRepository orderRepo, EmployeeRepository employeeRepo) {
        this.repo = repo; this.supplierRepo = supplierRepo; this.orderRepo = orderRepo; this.employeeRepo = employeeRepo;
    }

    @GetMapping
    public List<SupplierFeedback> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public SupplierFeedback get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("SupplierFeedback not found: " + id)); }

    @PostMapping
    public ResponseEntity<SupplierFeedback> create(@Valid @RequestBody SupplierFeedback f) {
        Supplier s = supplierRepo.findById(f.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + f.getSupplier().getId()));
        Order o = orderRepo.findById(f.getOrder().getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + f.getOrder().getId()));
        Employee e = employeeRepo.findById(f.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + f.getEmployee().getId()));
        f.setSupplier(s); f.setOrder(o); f.setEmployee(e);
        f.setCreatedAt(Instant.now());
        return ResponseEntity.ok(repo.save(f));
    }

    @PutMapping("/{id}")
    public SupplierFeedback update(@PathVariable Long id, @Valid @RequestBody SupplierFeedback incoming) {
        return repo.findById(id).map(f -> {
            if (incoming.getSupplier() != null && incoming.getSupplier().getId() != null) {
                Supplier s = supplierRepo.findById(incoming.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + incoming.getSupplier().getId()));
                f.setSupplier(s);
            }
            if (incoming.getOrder() != null && incoming.getOrder().getId() != null) {
                Order o = orderRepo.findById(incoming.getOrder().getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + incoming.getOrder().getId()));
                f.setOrder(o);
            }
            if (incoming.getEmployee() != null && incoming.getEmployee().getId() != null) {
                Employee e = employeeRepo.findById(incoming.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getEmployee().getId()));
                f.setEmployee(e);
            }
            f.setRating(incoming.getRating());
            f.setComments(incoming.getComments());
            f.setCreatedAt(Instant.now());
            return repo.save(f);
        }).orElseThrow(() -> new ResourceNotFoundException("SupplierFeedback not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(f -> { repo.delete(f); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("SupplierFeedback not found: " + id)); }
}

