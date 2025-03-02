package mqKafka.dynamic;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("dynamic")
public class DynamicController {

    private final DynamicService service;

    @GetMapping("register/{queue}/{topic}")
    public void register(
            @PathVariable("queue") String queueName,
            @PathVariable("topic") String topicName
    ) {
        service.register(queueName, topicName);
    }

    @DeleteMapping("unregister/{queue}")
    public void unregister(@PathVariable("queue") String queueName) {
        service.unregister(queueName);
    }
}
