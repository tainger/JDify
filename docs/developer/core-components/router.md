##Camel路由

首先看下复杂路由声明，假设有如下逻辑

```java

if() {
    
} else if () {
    
} else {
    
}

```

那么目前版本的配置声明为：

```json
{
    "listener":"",
    "endpoints":[
        {
            "type":"router-choice",
            "config":{
                "type":"when",
                "expression":"...",
                "endpoints":[

                ]
            }
        },
        {
            "type":"router-choice",
            "config":{
                "type":"otherwise",
                "expression":"",
                "endpoints":[
                    {
                        "type":"router-choice",
                        "config":{
                            "type":"when",
                            "expression":"...",
                            "endpoints":[

                            ]
                        }
                    },
                    {
                        "type":"router-choice",
                        "config":{
                            "type":"otherwise",
                            "expression":"...",
                            "endpoints":[

                            ]
                        }
                    }
                ]
            }
        }
    ]
}
```
其中expression为判定条件表达式（具体结构后续更新），endpoints为判定条件成立后执行的事件流


路由处理器接口实现：

```java
public class DalaranRouter implements DalaranProcessor<DalaranRouterConfig> {
    public void configure(ProcessorDefinition route, DalaranRouterConfig config) {
        ChoiceDefinition choice = route.choice();
        for (DalaranRouterConfig.Route routeConfig : config.getRoutes()) {
            ChoiceDefinition routeDefinition = choice.when(header("").isEqualTo(""));
            DalaranComponentContainer<DalaranProcessor> dalaranEndpointContainer = DalaranComponentLoader.getProcessorContainer(routeConfig.getProcessor().getType());
            dalaranEndpointContainer.getComponent().configure(routeDefinition, routeConfig.getProcessor().getConfig());
        }
    }
}
```



