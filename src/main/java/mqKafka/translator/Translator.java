package mqKafka.translator;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.kafka.producer.KafkaProducer;
import mqKafka.model.MessageDataModel;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
public class Translator implements MessageListener {

    private final KafkaProducer kafkaProducer;

    // TODO exactly 1 delivery
    // need to check JMS transactions
    // need to check KAFKA message acknowledgement

    // TODO message ordering
    // IBM MQ guarantees message ordering
    // KAFKA guarantees message ordering per partition, currently we write in a single partition

    @Override
    public void onMessage(Message jmsMessage) {
        try {
            kafkaProducer.sendMessage(jmsMessage.getBody(MessageDataModel.class));
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
