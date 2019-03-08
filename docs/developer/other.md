
## 已知风险点

1. 完全基于 camel, 目前没有做接口层屏蔽, 理论上来说, 做了意义也不大, 相当于又设计一层路由.
2. 可用配置是基于 config 模型类, 有些非 string 类型的参数, 目前的 property 替换方式是不支持的, 因为 json 内容会先转化为 config 对象, 类型不匹配情况下无法处理.
3. 对于 问题2, camel 本身有 {component}.json 文件来描述, 可用于配置, 如果考虑直接解析的话, 可能不用自己再封装一层. 有意思的是没有搜到类似文章使用或说明, 从代码来看, 也就 maven plugin 用了, 在官网也只找到一段描述.
    * Camel Catalog: Is a standalone JAR camel-catalog that contains catalog information about the Apache Camel release. Such as information about each of the Camel components, with documentation in json schema format. This is intended for SPI to leverage for tooling, such as being able to implement Apache Camel component editors that can provide the set of options the component offers, with documentation included.
4. 可视化方面, 除了 Fuse 之外, 没有找到合适的 editor. 如果要自己做, 目前设计的角度是基于自己设计的 json dsl, 而非 camel dsl.