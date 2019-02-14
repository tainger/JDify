package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranEndpoint
import io.terminus.dalaran.DalaranPropertyUtils
import org.apache.camel.model.RouteDefinition

@DalaranComponent("message-convert", configType = MessageConvertConfig::class)
class MessageConvertEndpoint : DalaranEndpoint<MessageConvertConfig> {

    private val uri = "dozer?targetModel=%s&mappingFile=test-mapping.xml"

    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: MessageConvertConfig) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.targetModel)
        route.to(uri)
    }
}