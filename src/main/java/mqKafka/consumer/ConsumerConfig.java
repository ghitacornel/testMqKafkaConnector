package mqKafka.consumer;

import lombok.extern.slf4j.Slf4j;
import mqKafka.config.JMSQueueConfig;
import mqKafka.model.MessageForQueue;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConsumerConfig {

    @JmsListener(destination = JMSQueueConfig.QUEUE_1)
    public void listenerForQueue1(MessageForQueue message) {
        log.info("Received on queue 1 {} thread {}", message, Thread.currentThread().getName());
    }

    @JmsListener(destination = JMSQueueConfig.QUEUE_2)
    public void listenerForQueue2(MessageForQueue message) {
        log.info("Received on queue 2 {} thread {}", message, Thread.currentThread().getName());
    }

}
