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
    public DefaultMessageListenerContainer defaultMessageListenerContainer1(
            @Qualifier("defaultJmsListenerContainerFactory") DefaultJmsListenerContainerFactory factory,
            @Qualifier("translator1") Translator translator
    ) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(translator);
        endpoint.setDestination(translator.getQueueName());
        DefaultMessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.setBeanName(translator.getQueueName());
        return listenerContainer;
    }

    @Bean
    public DefaultMessageListenerContainer defaultMessageListenerContainer2(
            @Qualifier("defaultJmsListenerContainerFactory") DefaultJmsListenerContainerFactory factory,
            @Qualifier("translator2") Translator translator
    ) {
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(translator);
        endpoint.setDestination(translator.getQueueName());
        DefaultMessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.setBeanName(translator.getQueueName());
        return listenerContainer;
    }

    @Bean
    Translator translator1(KafkaProducer kafkaProducer) {
        Translator translator = new Translator("queue1", kafkaProducer);
        return translator;
    }

    @Bean
    Translator translator2(KafkaProducer kafkaProducer) {
        Translator translator = new Translator("queue2", kafkaProducer);
        return translator;
    }

}
