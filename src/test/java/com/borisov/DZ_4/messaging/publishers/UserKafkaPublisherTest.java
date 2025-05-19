package com.borisov.DZ_4.messaging.publishers;

import com.borisov.DZ_4.messaging.events.UserCreatedEvent;
import com.borisov.DZ_4.messaging.events.UserDeletedEvent;
import com.borisov.DZ_4.messaging.events.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserKafkaPublisherTest {
    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final String topic = "test-topic";
    private UserKafkaPublisher publisher;

    @BeforeEach
    void setUp(){
        publisher = new UserKafkaPublisher(kafkaTemplate, topic);
    }

    @Test
    @DisplayName("onCreateUser should send CREATE event to Kafka")
    void onCreateUserTest() {
        UserCreatedEvent event = new UserCreatedEvent("test@example.com");
        publisher.onCreateUser(event);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaTemplate, times(1)).send(eq(topic), captor.capture());

        UserEvent sent = captor.getValue();
        assertEquals("test@example.com", sent.getEmail());
        assertEquals(UserEvent.Operation.CREATE, sent.getOperation());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDeleteUser(UserDeletedEvent e){
        kafkaTemplate.send(topic, new UserEvent(e.getEmail(), UserEvent.Operation.DELETE));
    }

    @Test
    @DisplayName("onCreateUser should send DELETE event to Kafka")
    void onDeleteUserTest() {
        UserDeletedEvent event = new UserDeletedEvent("test@example.com");
        publisher.onDeleteUser(event);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(kafkaTemplate, times(1)).send(eq(topic), captor.capture());

        UserEvent sent = captor.getValue();
        assertEquals("test@example.com", sent.getEmail());
        assertEquals(UserEvent.Operation.DELETE, sent.getOperation());
    }
}