package mqKafka.kafka.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    @Getter
    private final String topicName;

    private final KafkaTemplate<String, MessageDataModel> kafkaTemplate;

    public void sendMessage(MessageDataModel message) {
        kafkaTemplate.send(topicName, message);
    }

}
