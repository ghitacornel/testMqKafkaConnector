package mqKafka.dynamic;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("dynamic")
public class DynamicController {

    private final DynamicService service;

    @GetMapping("register/{queue}/{topic}")
    public void register(
            @PathVariable("queue") String queue,
            @PathVariable("topic") String topic
    ) {
        service.register(queue, topic);
    }

    @GetMapping("unregister/{queue}/{topic}")
    public void unregister(
            @PathVariable("queue") String queue,
            @PathVariable("topic") String topic
    ) {
        service.unregister(queue, topic);
    }
}
