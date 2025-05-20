/*
package com.borisov.DZ_4.messaging.publishers;

import borisov.core.UserChangedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserKafkaPublisherIT {

    private static final String TOPIC = "user-events-test";

    @Container
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
                    .asCompatibleSubstituteFor("confluentinc/cp-kafka")
    );

    static {
        kafka.start();
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("topic.user-events", () -> TOPIC);
        registry.add("spring.kafka.producer.key-serializer", () -> "org.apache.kafka.common.serialization.StringSerializer");
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.consumer.key-deserializer", () -> "org.apache.kafka.common.serialization.StringDeserializer");
        registry.add("spring.kafka.consumer.value-deserializer", () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
        registry.add("spring.kafka.consumer.properties.spring.json.trusted.packages", () -> "com.borisov.DZ_4.messaging.events");
    }

    @Autowired
    private UserKafkaPublisher publisher;

    @Autowired
    private KafkaTemplate<String, UserChangedEvent> kafkaTemplate;

    private Consumer<String, UserChangedEvent> consumer;

    @BeforeAll
    void setUpConsumer() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.borisov.DZ_4.messaging.events");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, UserChangedEvent> factory =
                new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                        new JsonDeserializer<>(UserChangedEvent.class));

        consumer = factory.createConsumer();
        consumer.subscribe(Collections.singletonList(TOPIC));
        consumer.poll(Duration.ofMillis(100));
    }

    @AfterAll
    void tearDown() {
        consumer.close();
        kafka.stop();
    }

    @Test
    void testCreateUserPublishesEvent() {
        int id = 4;
        String email = "test@domain.com";
        publisher.onCreateUser(new UserChangedEvent(id, email, UserChangedEvent.Operation.CREATE));
        kafkaTemplate.flush();

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    ConsumerRecords<String, UserChangedEvent> records = consumer.poll(Duration.ofMillis(500));
                    assertEquals(1, records.count());
                    UserChangedEvent evt = records.iterator().next().value();
                    assertEquals(String.valueOf(id), records.iterator().next().key());
                    assertEquals(email, evt.getEmail());
                    assertEquals(UserChangedEvent.Operation.CREATE, evt.getOperation());
                });
    }
}
*/
