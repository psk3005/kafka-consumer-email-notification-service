package com.praveen.kafka.example.kafka_consumer_email_notification_service.error;

public class RetryableException extends RuntimeException{

    public RetryableException(String message) {
        super(message);
    }

    public RetryableException(Throwable cause) {
        super(cause);
    }
}
