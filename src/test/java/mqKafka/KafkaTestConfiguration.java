package mqKafka;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class KafkaTestConfiguration {

//    @Bean
//    EmbeddedKafkaBroker broker() {
//        Map<String, String> properties = new HashMap<>();
//        properties.put("listeners", "PLAINTEXT://localhost:9092,REMOTE://10.0.0.20:9093");
//        properties.put("advertised.listeners", "PLAINTEXT://localhost:9092,REMOTE://10.0.0.20:9093");
//        properties.put("listener.security.protocol.map", "PLAINTEXT:PLAINTEXT,REMOTE:PLAINTEXT");
//        return new EmbeddedKafkaZKBroker(1)
//                .kafkaPorts(9092)
//                .brokerProperties(properties)
//                .brokerListProperty("spring.kafka.bootstrap-servers");
//    }

    @Bean
    public BrokerService brokerService() throws Exception {
        BrokerService broker = new BrokerService();
        TransportConnector connector = new TransportConnector();
        connector.setUri(new URI("tcp://localhost:61616"));
        broker.addConnector(connector);
        broker.start();
        return broker;
    }

}
