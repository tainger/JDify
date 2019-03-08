package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranExecutor
import io.terminus.dalaran.DalaranPropertyUtils
import org.apache.camel.model.RouteDefinition

@DalaranComponent("message-convert", configType = MessageConvertConfig::class)
class MessageConvertExecutor : DalaranExecutor<MessageConvertConfig> {

    private val uri = "dozer?targetModel=%s&mappingFile=%s"

    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: MessageConvertConfig) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.targetModel, config.mappingFile)
        route.to(uri)
    }
}