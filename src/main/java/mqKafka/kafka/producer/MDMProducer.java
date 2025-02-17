package mqKafka.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MDMProducer {

    @Value(value = "${kafka.mdmTopicName}")
    private String topicName;

    private final KafkaTemplate<String, MessageDataModel> kafkaTemplate;

    public void sendMessage(MessageDataModel message) {
        log.info("Kafka produced message : {}", message);
        kafkaTemplate.send(topicName, message);
    }

}
