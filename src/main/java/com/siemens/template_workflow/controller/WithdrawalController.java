package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Withdrawal;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.model.Inventory;
import com.siemens.template_workflow.repository.WithdrawalRepository;
import com.siemens.template_workflow.repository.EmployeeRepository;
import com.siemens.template_workflow.repository.InventoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {
    private final WithdrawalRepository repo;
    private final EmployeeRepository employeeRepo;
    private final InventoryRepository inventoryRepo;

    public WithdrawalController(WithdrawalRepository repo, EmployeeRepository employeeRepo, InventoryRepository inventoryRepo) {
        this.repo = repo; this.employeeRepo = employeeRepo; this.inventoryRepo = inventoryRepo;
    }

    @GetMapping
    public List<Withdrawal> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Withdrawal get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found: " + id)); }

    @PostMapping
    public ResponseEntity<Withdrawal> create(@Valid @RequestBody Withdrawal w) {
        Employee requester = employeeRepo.findById(w.getRequestedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + w.getRequestedBy().getId()));
        Inventory item = inventoryRepo.findById(w.getItem().getId()).orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + w.getItem().getId()));
        w.setRequestedBy(requester); w.setItem(item);
        w.setRequestedAt(Instant.now());
        if (w.getApprovalStatus() == null) w.setApprovalStatus("pending");
        return ResponseEntity.ok(repo.save(w));
    }

    @PutMapping("/{id}")
    public Withdrawal update(@PathVariable Long id, @Valid @RequestBody Withdrawal incoming) {
        return repo.findById(id).map(w -> {
            if (incoming.getRequestedBy() != null && incoming.getRequestedBy().getId() != null) {
                Employee r = employeeRepo.findById(incoming.getRequestedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getRequestedBy().getId()));
                w.setRequestedBy(r);
            }
            if (incoming.getItem() != null && incoming.getItem().getId() != null) {
                Inventory it = inventoryRepo.findById(incoming.getItem().getId()).orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + incoming.getItem().getId()));
                w.setItem(it);
            }
            w.setQuantity(incoming.getQuantity());
            if (incoming.getApprovedBy() != null && incoming.getApprovedBy().getId() != null) {
                Employee a = employeeRepo.findById(incoming.getApprovedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getApprovedBy().getId()));
                w.setApprovedBy(a);
            } else { w.setApprovedBy(null); }
            w.setApprovalStatus(incoming.getApprovalStatus());
            w.setApprovedAt(incoming.getApprovedAt());
            w.setRequestedAt(incoming.getRequestedAt());
            return repo.save(w);
        }).orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(w -> { repo.delete(w); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Withdrawal not found: " + id)); }
}

