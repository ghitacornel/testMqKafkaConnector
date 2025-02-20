package mqKafka.translator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.kafka.producer.KafkaProducer;
import mqKafka.model.MessageDataModel;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TranslatorConfig {

    private final KafkaProducer kafkaProducer;

    // TODO exactly 1 delivery
    // need to check JMS transactions
    // need to check KAFKA message acknowledgement

    // TODO message ordering
    // IBM MQ guarantees message ordering
    // KAFKA guarantees message ordering per partition, currently we write in a single partition

    @JmsListener(destination = "queue1")
    public void listener1(MessageDataModel message) {
        kafkaProducer.sendMessage(message);
    }

    @JmsListener(destination = "queue2")
    public void listener2(MessageDataModel message) {
        kafkaProducer.sendMessage(message);
    }

}
