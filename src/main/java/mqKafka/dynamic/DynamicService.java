package mqKafka.dynamic;

import jakarta.jms.JMSException;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mqKafka.jms.producer.JMSProducerThread;
import mqKafka.kafka.producer.KafkaProducer;
import mqKafka.model.MessageDataModel;
import mqKafka.translator.Translator;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicService {

    private final ApplicationContext applicationContext;
    private final JmsTemplate jmsTemplate;
    private final KafkaTemplate<String, MessageDataModel> kafkaTemplate;

    @Qualifier("defaultJmsListenerContainerFactory")
    private final DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory;

    public void register(String queueName, String topicName) {
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

        // create Kafka topic
        {
            NewTopic existingTopic = applicationContext.getBeansOfType(NewTopic.class).values().stream()
                    .filter(newTopic -> newTopic.name().equals(topicName))
                    .findFirst()
                    .orElse(null);
            if (existingTopic == null) {
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(NewTopic.class)
                        .addConstructorArgValue(topicName)
                        .addConstructorArgValue(1)
                        .addConstructorArgValue(1)
                        .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                        .getBeanDefinition();
                factory.registerBeanDefinition(topicName, beanDefinition);
                log.info("Kafka topic {} registered", topicName);

            }
        }

        // create Kafka producer
        {
            KafkaProducer existingKafkaProducer = applicationContext.getBeansOfType(KafkaProducer.class).values().stream()
                    .filter(kafkaProducer -> kafkaProducer.getTopicName().equals(topicName))
                    .findFirst()
                    .orElse(null);
            if (existingKafkaProducer == null) {
                AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KafkaProducer.class)
                        .addConstructorArgValue(topicName)
                        .addConstructorArgValue(kafkaTemplate)
                        .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                        .getBeanDefinition();
                factory.registerBeanDefinition("producerForTopic_" + topicName, beanDefinition);
                log.info("Kafka producer for topic {} registered", topicName);
            }
        }

        // create translator
        {
            KafkaProducer existingKafkaProducer = applicationContext.getBeansOfType(KafkaProducer.class).values().stream()
                    .filter(kafkaProducer -> kafkaProducer.getTopicName().equals(topicName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Kafka producer with name " + topicName + " not found"));
            AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Translator.class)
                    .addConstructorArgValue(queueName)
                    .addConstructorArgValue(existingKafkaProducer)
                    .setScope(AbstractBeanDefinition.SCOPE_SINGLETON)
                    .getBeanDefinition();
            factory.registerBeanDefinition("translatorForQueue_" + queueName, beanDefinition);
            log.info("queue translator {} registered", queueName);
        }

        // create listener container
        {

            // get previously build translator
            Translator createdTranslator = applicationContext.getBeansOfType(Translator.class).values().stream()
                    .filter(translator -> translator.isForQueue(queueName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("no producer found for " + queueName));

            // build listener container
            DefaultMessageListenerContainer defaultMessageListenerContainer = buildListenerContainer(defaultJmsListenerContainerFactory, createdTranslator);
            defaultMessageListenerContainer.start();

            GenericWebApplicationContext genericWebApplicationContext = (GenericWebApplicationContext) applicationContext;
            genericWebApplicationContext.registerBean("listenerContainerForQueue_" + queueName, DefaultMessageListenerContainer.class, () -> defaultMessageListenerContainer);
            log.info("queue listener container {} registered", queueName);
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
                    entry.getValue().stop();
                    entry.getValue().shutdown();
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

    private static DefaultMessageListenerContainer buildListenerContainer(
            DefaultJmsListenerContainerFactory factory,
            Translator translator
    ) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(translator);
        endpoint.setDestination(translator.getQueueName());
        DefaultMessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.setBeanName(translator.getQueueName());
        listenerContainer.setMessageListener(translator);
        listenerContainer.afterPropertiesSet();
        listenerContainer.start();
        return listenerContainer;
    }

}
