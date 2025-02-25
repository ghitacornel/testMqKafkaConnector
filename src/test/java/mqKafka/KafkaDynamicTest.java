package mqKafka;

import mqKafka.dynamic.DynamicService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;

@EnableKafka
@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        controlledShutdown = true,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:29092",
                "port=29092"
        })
public class KafkaDynamicTest {

    @Autowired
    DynamicService dynamicService;

    @Test
    public void testKafkaDynamic() {
        dynamicService.unregister("queue1");
        dynamicService.unregister("queue2");
    }
}
