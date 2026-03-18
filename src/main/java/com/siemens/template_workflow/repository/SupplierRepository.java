package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}

