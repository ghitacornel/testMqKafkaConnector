package mqKafka.jms.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
class JMSProducerConfig {

    private final JMSProducer JMSProducer;

    @Bean
    Thread producerThread1() {
        Thread thread = new Thread(() -> {
            while (true) {
                JMSProducer.createMessageAndSendItToTheQueue1();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });
        thread.setName("Producer Thread 1");
        thread.start();
        return thread;
    }

    @Bean
    Thread producerThread2() {
        Thread thread = new Thread(() -> {
            while (true) {
                JMSProducer.createMessageAndSendItToTheQueue2();
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
            }
        });
        thread.setName("Producer Thread 2");
        thread.start();
        return thread;
    }

}
