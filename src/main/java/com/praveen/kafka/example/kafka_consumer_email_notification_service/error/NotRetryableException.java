package com.praveen.kafka.example.kafka_consumer_email_notification_service.error;

public class NotRetryableException extends RuntimeException{

    public NotRetryableException(String message) {
        super(message);
    }

    public NotRetryableException(Throwable cause) {
        super(cause);
    }
}
