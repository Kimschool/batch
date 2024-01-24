package com.example.SpringBatchTutorial.core.domain.mail;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MailHistoryRepository extends JpaRepository<MailHistory, Integer> {
}
