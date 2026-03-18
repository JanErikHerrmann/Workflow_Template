package com.siemens.template_workflow.controller;

import com.siemens.template_workflow.exception.ResourceNotFoundException;
import com.siemens.template_workflow.model.Document;
import com.siemens.template_workflow.model.Employee;
import com.siemens.template_workflow.model.Order;
import com.siemens.template_workflow.repository.DocumentRepository;
import com.siemens.template_workflow.repository.EmployeeRepository;
import com.siemens.template_workflow.repository.OrderRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentRepository repo;
    private final EmployeeRepository employeeRepo;
    private final OrderRepository orderRepo;

    public DocumentController(DocumentRepository repo, EmployeeRepository employeeRepo, OrderRepository orderRepo) {
        this.repo = repo; this.employeeRepo = employeeRepo; this.orderRepo = orderRepo;
    }

    @GetMapping
    public List<Document> all() { return repo.findAll(); }

    @GetMapping("/{id}")
    public Document get(@PathVariable Long id) { return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id)); }

    @PostMapping
    public ResponseEntity<Document> create(@Valid @RequestBody Document d) {
        Employee u = employeeRepo.findById(d.getUploadedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + d.getUploadedBy().getId()));
        d.setUploadedBy(u);
        if (d.getOrder() != null && d.getOrder().getId() != null) {
            Order o = orderRepo.findById(d.getOrder().getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + d.getOrder().getId()));
            d.setOrder(o);
        }
        d.setUploadedAt(Instant.now());
        return ResponseEntity.ok(repo.save(d));
    }

    @PutMapping("/{id}")
    public Document update(@PathVariable Long id, @Valid @RequestBody Document incoming) {
        return repo.findById(id).map(d -> {
            d.setFileName(incoming.getFileName());
            d.setFileType(incoming.getFileType());
            d.setFileSize(incoming.getFileSize());
            if (incoming.getUploadedBy() != null && incoming.getUploadedBy().getId() != null) {
                Employee u = employeeRepo.findById(incoming.getUploadedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + incoming.getUploadedBy().getId()));
                d.setUploadedBy(u);
            }
            if (incoming.getOrder() != null && incoming.getOrder().getId() != null) {
                Order o = orderRepo.findById(incoming.getOrder().getId()).orElseThrow(() -> new ResourceNotFoundException("Order not found: " + incoming.getOrder().getId()));
                d.setOrder(o);
            } else { d.setOrder(null); }
            d.setUploadedAt(Instant.now());
            return repo.save(d);
        }).orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) { return repo.findById(id).map(d -> { repo.delete(d); return ResponseEntity.noContent().build(); }).orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id)); }
}

