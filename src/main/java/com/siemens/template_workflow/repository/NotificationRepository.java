package com.siemens.template_workflow.repository;

import com.siemens.template_workflow.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}

