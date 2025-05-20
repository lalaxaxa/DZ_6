/*
package com.borisov.DZ_4.messaging.publishers;

import com.borisov.DZ_4.messaging.events.UserChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserKafkaPublisherTest {
    @Mock
    private KafkaTemplate<String, UserChangedEvent> kafkaTemplate;
    private final String topic = "test-topic";
    private UserKafkaPublisher publisher;

    @BeforeEach
    void setUp(){
        publisher = new UserKafkaPublisher(kafkaTemplate, topic);
    }

    @Test
    @DisplayName("onCreateUser should send CREATE event to Kafka")
    void onCreateUserTest() {
        UserChangedEvent event = new UserChangedEvent(33, "test@example.com", UserChangedEvent.Operation.CREATE);
        CompletableFuture<SendResult<String, UserChangedEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send()).thenReturn(completableFuture);
        publisher.onCreateUser(event);

        ArgumentCaptor<UserChangedEvent> captor = ArgumentCaptor.forClass(UserChangedEvent.class);
        verify(kafkaTemplate, times(1)).send(eq(topic), eq("33"), captor.capture());

        UserChangedEvent sent = captor.getValue();
        assertEquals("test@example.com", sent.getEmail());
        assertEquals(UserChangedEvent.Operation.CREATE, sent.getOperation());
    }

}*/
