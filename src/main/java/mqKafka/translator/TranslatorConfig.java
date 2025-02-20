package mqKafka.translator;

import jakarta.jms.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import mqKafka.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
@RequiredArgsConstructor
class TranslatorConfig {

    @Bean
    DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrency("1");
        return factory;
    }

    @Bean
    SimpleJmsListenerContainerFactory simpleJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    DefaultMessageListenerContainer listenerContainerForQueue_queue1(
            @Qualifier("defaultJmsListenerContainerFactory") DefaultJmsListenerContainerFactory factory,
            @Qualifier("translatorForQueue_queue1") Translator translator
    ) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(translator);
        endpoint.setDestination(translator.getQueueName());
        DefaultMessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.setBeanName(translator.getQueueName());
        return listenerContainer;
    }

    @Bean
    DefaultMessageListenerContainer listenerContainerForQueue_queue2(
            @Qualifier("defaultJmsListenerContainerFactory") DefaultJmsListenerContainerFactory factory,
            @Qualifier("translatorForQueue_queue2") Translator translator
    ) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(translator);
        endpoint.setDestination(translator.getQueueName());
        DefaultMessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.setBeanName(translator.getQueueName());
        return listenerContainer;
    }

    @Bean
    Translator translatorForQueue_queue1(KafkaProducer kafkaProducer) {
        return new Translator("queue1", kafkaProducer);
    }

    @Bean
    Translator translatorForQueue_queue2(KafkaProducer kafkaProducer) {
        return new Translator("queue2", kafkaProducer);
    }

}
