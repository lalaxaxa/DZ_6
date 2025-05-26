package com.borisov.DZ_4.messaging.publishers;


import borisov.core.UserChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = true)

public class UserKafkaPublisher {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, UserChangedEvent> kafkaTemplate;
    private final String topic;

    @Autowired
    public UserKafkaPublisher(KafkaTemplate<String, UserChangedEvent> kafkaTemplate,
                              @Value("${topic.user-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateUser(UserChangedEvent event) {
        CompletableFuture<SendResult<String, UserChangedEvent>> future =
                kafkaTemplate.send(topic, String.valueOf(event.getId()), event);

        future.whenComplete((result, exception) -> {
            if (exception != null){
                LOGGER.error("Failed to send message: " + exception.getMessage());
            } else{
                LOGGER.info("Message sent successfully:"
                        + " Topic=" + result.getRecordMetadata().topic()
                        + " Partition=" + result.getRecordMetadata().partition()
                        + " Offset=" + result.getRecordMetadata().offset()
                        );
            }
        });
        //future.join(); // ;ждать завершения отправки сообщения
        //или так
        //SendResult<String, UserChangedEvent> result = kafkaTemplate.send(topic, String.valueOf(event.getId()), event).get();
    }
}
