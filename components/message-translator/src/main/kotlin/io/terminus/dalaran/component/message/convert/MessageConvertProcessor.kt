package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.annotation.DalaranComponent
import io.terminus.dalaran.DalaranProcessor
import io.terminus.dalaran.DalaranPropertyUtils
import org.apache.camel.model.RouteDefinition

@DalaranComponent("message-convert", configType = MessageConvertConfig::class)
class MessageConvertProcessor : DalaranProcessor<MessageConvertConfig> {

    private val uri = "dozer?targetModel=%s&mappingFile=%s"

    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: MessageConvertConfig) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.targetModel, config.mappingFile)
        route.to(uri)
    }
}