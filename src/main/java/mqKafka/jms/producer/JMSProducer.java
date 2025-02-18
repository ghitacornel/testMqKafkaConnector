package mqKafka.jms.producer;

import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class JMSProducer {

    @Qualifier("queue1")
    private final Queue queue1;
    private final AtomicInteger counter1 = new AtomicInteger(0);

    @Qualifier("queue2")
    private final Queue queue2;
    private final AtomicInteger counter2 = new AtomicInteger(0);

    private final JmsTemplate jmsTemplate;

    public void createMessageAndSendItToTheQueue1() {
        MessageDataModel message = new MessageDataModel(counter1.getAndIncrement(), "payload for queue 1 " + new Date().getTime());
        log.info("JMS to queue 1: {} , thread {}", message, Thread.currentThread().getName());
        jmsTemplate.convertAndSend(queue1, message);
    }

    public void createMessageAndSendItToTheQueue2() {
        MessageDataModel message = new MessageDataModel(counter2.getAndIncrement(), "payload for queue 2" + new Date().getTime());
        log.info("JMS to queue 2: {} , thread {}", message, Thread.currentThread().getName());
        jmsTemplate.convertAndSend(queue2, message);
    }

}
