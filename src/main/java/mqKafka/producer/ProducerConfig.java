package mqKafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
class ProducerConfig {

    private final Producer producer;

    @Bean
    Thread producerThread1() {
        Thread thread = new Thread(() -> {
            while (true) {
                producer.createMessageAndSendItToTheQueue1();
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
                producer.createMessageAndSendItToTheQueue2();
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
