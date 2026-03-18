package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}

