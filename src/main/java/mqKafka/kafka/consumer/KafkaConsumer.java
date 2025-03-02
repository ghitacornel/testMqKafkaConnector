package mqKafka.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class KafkaConsumer {

    @KafkaListener(
            topics = "topic1",
            groupId = "groupId",
            containerFactory = "kafkaListenerDataModelContainerFactory"
    )
    void consumeMessage1(MessageDataModel message) {
        log.info("Kafka 1 consumed message : {}", message);
    }

    @KafkaListener(
            topics = "topic2",
            groupId = "groupId",
            containerFactory = "kafkaListenerDataModelContainerFactory"
    )
    void consumeMessage2(MessageDataModel message) {
        log.info("Kafka 2 consumed message : {}", message);
    }

    @KafkaListener(
            topics = "topic3",
            groupId = "groupId",
            containerFactory = "kafkaListenerDataModelContainerFactory"
    )
    void consumeMessage3(MessageDataModel message) {
        log.info("Kafka 3 consumed message : {}", message);
    }

}
