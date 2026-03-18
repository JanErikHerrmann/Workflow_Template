package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}

