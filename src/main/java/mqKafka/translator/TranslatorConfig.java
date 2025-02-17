package mqKafka.translator;

import lombok.extern.slf4j.Slf4j;
import mqKafka.jms.config.JMSQueueConfig;
import mqKafka.model.MessageDataModel;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TranslatorConfig {

    @JmsListener(destination = JMSQueueConfig.QUEUE_1)
    public void listenerForQueue1(MessageDataModel message) {
        log.info("Received on queue 1 {} thread {}", message, Thread.currentThread().getName());
    }

    @JmsListener(destination = JMSQueueConfig.QUEUE_2)
    public void listenerForQueue2(MessageDataModel message) {
        log.info("Received on queue 2 {} thread {}", message, Thread.currentThread().getName());
    }

}
