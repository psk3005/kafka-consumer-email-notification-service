package com.praveen.kafka.example.kafka_consumer_email_notification_service.handler;

import com.praveen.kafka.example.kafka_consumer_email_notification_service.error.NotRetryableException;
import com.praveen.kafka.example.kafka_consumer_email_notification_service.io.ProcessedEventEntity;
import com.praveen.kafka.example.kafka_consumer_email_notification_service.io.ProcessedEventRepository;
import com.praveen.kafka.example.kafka_core.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics="product-created-events-topic")
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private RestTemplate restTemplate;
    private ProcessedEventRepository processedEventRepository;

    public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository) {
        this.restTemplate = restTemplate;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        LOGGER.info("Received a new event: {} with product id {}", productCreatedEvent.getTitle(), productCreatedEvent.getProductId());

        //check if this message was already processed before
        ProcessedEventEntity existingRecord = processedEventRepository.findByMessageId(messageId);
        if (existingRecord != null) {
            LOGGER.info("Found a duplicate message id: {}", existingRecord.getMessageId());
            return;
        }


        /*String requestUrl = "http://localhost:8082/response/500";
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if(response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response from remote service : {}", response.getBody());
            }
        } catch (ResourceAccessException ex) {
            LOGGER.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch (HttpServerErrorException ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }*/

        //save unique message id in database table
        try {
            processedEventRepository.save(new ProcessedEventEntity(messageId, productCreatedEvent.getProductId()));
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }

    }

}
