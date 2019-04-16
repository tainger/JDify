package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.DalaranContext;
import io.terminus.dalaran.model.DalaranTracingLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tracing")
public class TracingRest {

    @Autowired
    private DalaranContext dalaranContext;

    @PostMapping("/{flow}/test")
    private Object doTest(@PathVariable Long flowId, @RequestBody String body) {

        // TODO test flow 什么时候 load 是个问题, 主要是修改后的 flow
        Object data = dalaranContext.testFlow(flowId, body);
        // TODO 如果最后一步是序列化的情况, 会被序列化成 byte[], 先 toString 一下
        if (data instanceof byte[]) {
            return new String((byte[]) data);
        }
        return data;
    }

    @GetMapping("/{flow}")
    private List<DalaranTracingLog> queryFlow() {


        return null;
    }
}
