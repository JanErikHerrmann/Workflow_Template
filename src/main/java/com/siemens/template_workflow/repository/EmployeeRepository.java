package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	java.util.Optional<Employee> findByUsername(String username);
	java.util.Optional<Employee> findByEmail(String email);
}

