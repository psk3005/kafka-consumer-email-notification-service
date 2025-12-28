package com.praveen.kafka.example.kafka_consumer_email_notification_service.io;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessedEventRepository  extends JpaRepository<ProcessedEventEntity, Long> {

    ProcessedEventEntity findByMessageId(String messageId);
}
