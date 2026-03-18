package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Inventory;
import com.siemens.template_workflow.model.Order;
import com.siemens.template_workflow.model.Supplier;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.model.OrderStatus;
import com.siemens.template_workflow.repository.InventoryRepository;
import com.siemens.template_workflow.repository.OrderRepository;
import com.siemens.template_workflow.repository.SupplierRepository;
import com.siemens.template_workflow.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository repo;
    private final InventoryRepository inventoryRepo;
    private final SupplierRepository supplierRepo;
    private final EmployeeRepository employeeRepo;

    public OrderController(OrderRepository repo, InventoryRepository inventoryRepo, SupplierRepository supplierRepo, EmployeeRepository employeeRepo) {
        this.repo = repo; this.inventoryRepo = inventoryRepo; this.supplierRepo = supplierRepo; this.employeeRepo = employeeRepo;
    }

    @GetMapping
    public List<Order> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Order get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)); }

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody Order o) {
        // validate foreign references
        Inventory item = inventoryRepo.findById(o.getItem().getId()).orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + o.getItem().getId()));
        Employee emp = employeeRepo.findById(o.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + o.getEmployee().getId()));
        Supplier sup = supplierRepo.findById(o.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + o.getSupplier().getId()));
        o.setItem(item); o.setEmployee(emp); o.setSupplier(sup);
        o.setCreatedAt(Instant.now()); o.setUpdatedAt(Instant.now());
        if (o.getStatus() == null) o.setStatus(OrderStatus.pending);
        return ResponseEntity.ok(repo.save(o));
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @Valid @RequestBody Order incoming) {
        return repo.findById(id).map(o -> {
            if (incoming.getItem() != null && incoming.getItem().getId() != null) {
                Inventory item = inventoryRepo.findById(incoming.getItem().getId()).orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + incoming.getItem().getId()));
                o.setItem(item);
            }
            if (incoming.getEmployee() != null && incoming.getEmployee().getId() != null) {
                Employee emp = employeeRepo.findById(incoming.getEmployee().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getEmployee().getId()));
                o.setEmployee(emp);
            }
            if (incoming.getSupplier() != null && incoming.getSupplier().getId() != null) {
                Supplier sup = supplierRepo.findById(incoming.getSupplier().getId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + incoming.getSupplier().getId()));
                o.setSupplier(sup);
            }
            o.setQuantity(incoming.getQuantity());
            o.setStatus(incoming.getStatus());
            o.setPriority(incoming.getPriority());
            o.setExpectedDeliveryDate(incoming.getExpectedDeliveryDate());
            o.setActualDeliveryDate(incoming.getActualDeliveryDate());
            o.setUpdatedAt(Instant.now());
            return repo.save(o);
        }).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(o -> { repo.delete(o); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id)); }
}

