package mqKafka.dynamic;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.jms.producer.JMSProducerThread;
import mqKafka.translator.Translator;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicService {

    private final ApplicationContext applicationContext;
    private final JmsTemplate jmsTemplate;

    public void register(String queueName, String topic) {
        BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        // create queue
        {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ActiveMQQueue.class)
                    .addConstructorArgValue(queueName)
                    .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                    .getBeanDefinition();
            factory.registerBeanDefinition(queueName, beanDefinition);
            log.info("queue {} registered", queueName);
        }

        // create queue producer
        {
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(JMSProducerThread.class)
                    .addConstructorArgValue(applicationContext.getBeansOfType(Queue.class).values().stream()
                            .filter(queue -> {
                                try {
                                    return queue.getQueueName().equals(queueName);
                                } catch (JMSException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("queue with name " + queueName + " not found")))
                    .addConstructorArgValue(jmsTemplate)
                    .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                    .getBeanDefinition();
            factory.registerBeanDefinition("producerForQueue_" + queueName, beanDefinition);
            log.info("queue producer {} registered", queueName);
        }

        // start queue producer
        {
            applicationContext.getBeansOfType(JMSProducerThread.class).values().stream()
                    .filter(producerThread -> producerThread.isForQueue(queueName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("no producer found for " + queueName))
                    .start();
            log.info("queue producer {} started", queueName);
        }

    }

    public void unregister(String queueName) {
        BeanDefinitionRegistry factory = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();

        // remove producer
        applicationContext.getBeansOfType(JMSProducerThread.class)
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isForQueue(queueName))
                .findFirst()
                .ifPresent(entry -> {
                    factory.removeBeanDefinition(entry.getKey());
                    entry.getValue().cancel();
                    log.info("unregister Producer for queue name: {}", queueName);
                });

        // remove listener container
        applicationContext.getBeansOfType(DefaultMessageListenerContainer.class)
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue().getDestinationName(), queueName))
                .findFirst()
                .ifPresent(entry -> {
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister DefaultMessageListenerContainer for queue name: {}", queueName);
                });

        // remove translator
        applicationContext.getBeansOfType(Translator.class)
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue().getQueueName(), queueName))
                .findFirst()
                .ifPresent(entry -> {
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister Translator for queue name: {}", queueName);
                });

        // remove queue
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
                    factory.removeBeanDefinition(entry.getKey());
                    log.info("unregister Queue for queue name: {}", queueName);
                });

    }

}
