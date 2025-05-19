package com.borisov.DZ_4.messaging.publishers;

import com.borisov.DZ_4.messaging.events.UserCreatedEvent;
import com.borisov.DZ_4.messaging.events.UserDeletedEvent;
import com.borisov.DZ_4.messaging.events.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserKafkaPublisher {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String topic;

    @Autowired
    public UserKafkaPublisher(KafkaTemplate<String, UserEvent> kafkaTemplate,
                              @Value("${topic.user-events}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreateUser(UserCreatedEvent e){
        kafkaTemplate.send(topic, new UserEvent(e.getEmail(), UserEvent.Operation.CREATE));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleteUser(UserDeletedEvent e){
        kafkaTemplate.send(topic, new UserEvent(e.getEmail(), UserEvent.Operation.DELETE));
    }
}
