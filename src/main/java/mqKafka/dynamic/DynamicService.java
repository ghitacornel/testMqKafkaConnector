package mqKafka.dynamic;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.jms.producer.JMSProducerThread;
import mqKafka.translator.Translator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicService {

    private final ApplicationContext applicationContext;

    public void register(String queue, String topic) {
        System.out.println("register income " + queue + " to topic " + topic);
    }

    public void unregister(String queueName, String topic) {
        applicationContext.getBeansOfType(JMSProducerThread.class)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isForQueue(queueName))
                .findFirst()
                .ifPresent(entry -> {
                    BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
                    factory.removeBeanDefinition(entry.getKey());
                    entry.getValue().cancel();
                    log.info("unregister Producer for queue name: {}", queueName);
                });
        applicationContext.getBeansOfType(Queue.class)
                .entrySet()
                .stream()
                .filter(entry -> {
                    try {
                        return entry.getValue().getQueueName().equals(queueName);
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .ifPresent(entry -> {
                    BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister Queue for queue name: {}", queueName);
                });
        applicationContext.getBeansOfType(DefaultMessageListenerContainer.class)
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue().getDestinationName(), queueName))
                .findFirst()
                .ifPresent(entry -> {
                    BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister DefaultMessageListenerContainer for queue name: {}", queueName);
                });
        applicationContext.getBeansOfType(Translator.class)
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue().getQueueName(), queueName))
                .findFirst()
                .ifPresent(entry -> {
                    BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister Translator for queue name: {}", queueName);
                });
    }

}
