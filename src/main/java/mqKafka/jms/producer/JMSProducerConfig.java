package mqKafka.jms.producer;

import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
class JMSProducerConfig {

    @Bean
    JMSProducerThread producerThread1(@Qualifier("queue1") Queue queue, JmsTemplate jmsTemplate) {
        JMSProducerThread thread = new JMSProducerThread(queue, jmsTemplate);
        thread.start();
        return thread;
    }

    @Bean
    JMSProducerThread producerThread2(@Qualifier("queue2") Queue queue, JmsTemplate jmsTemplate) {
        JMSProducerThread thread = new JMSProducerThread(queue, jmsTemplate);
        thread.start();
        return thread;
    }

}
