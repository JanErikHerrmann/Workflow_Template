package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}

