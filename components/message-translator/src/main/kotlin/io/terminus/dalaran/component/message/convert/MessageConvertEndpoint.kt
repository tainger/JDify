package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranEndpoint
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

class MessageConvertEndpoint : DalaranEndpoint {
    override fun getType() = "message-convert"

    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        TODO("dozer...")
    }
}