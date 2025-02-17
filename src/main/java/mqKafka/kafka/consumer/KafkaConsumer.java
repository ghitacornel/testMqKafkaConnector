package mqKafka.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "mdmTopicName", groupId = "groupId")
    public void consumeMessage(MessageDataModel message) {
        log.info("Kafka consumed message : {}", message);
    }

}
