package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

