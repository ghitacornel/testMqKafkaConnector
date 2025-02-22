package mqKafka.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TopicConfiguration {

    @Value(value = "${kafka.mdmTopicName}")
    private String mdmTopicName;

    @Bean
    NewTopic mdmTopic() {
        return new NewTopic(mdmTopicName, 1, (short) 1);
    }

}