package mqKafka.jms.producer;

import jakarta.jms.Queue;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mqKafka.model.MessageDataModel;
import org.springframework.jms.core.JmsTemplate;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class JMSProducerThread extends Thread {

    private final Queue queue;
    private final String queueName;
    private final JmsTemplate jmsTemplate;

    private final AtomicInteger counter = new AtomicInteger(0);
    private boolean running = true;

    @SneakyThrows
    public JMSProducerThread(Queue queue, JmsTemplate jmsTemplate) {
        this.queue = queue;
        this.jmsTemplate = jmsTemplate;
        this.queueName = queue.getQueueName();
    }

    @Override
    public void run() {
        while (running) {
            createMessageAndSendItToTheQueue();
        }
    }

    @SneakyThrows
    private void createMessageAndSendItToTheQueue() {
        MessageDataModel message = new MessageDataModel(counter.getAndIncrement(), "payload for " + queue.getQueueName() + " " + new Date().getTime());
        log.info("JMS to {} : {} , thread {}", queueName, message, Thread.currentThread().getName());
        jmsTemplate.convertAndSend(queue, message);
    }

    public void cancel() {
        running = false;
    }

    public boolean isForQueue(String queueName) {
        return this.queueName.equals(queueName);
    }

}
