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
            @PathVariable("queue") String queue,
            @PathVariable("topic") String topic
    ) {
        service.register(queue, topic);
    }

    @DeleteMapping("unregister/{queue}")
    public void unregister(@PathVariable("queue") String queue) {
        service.unregister(queue);
    }
}
