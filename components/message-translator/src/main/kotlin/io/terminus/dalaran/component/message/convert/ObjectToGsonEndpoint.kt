package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranEndpoint
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

class ObjectToGsonEndpoint : DalaranEndpoint {
    override fun getType() = "object-to-json"

    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        route.marshal().json(JsonLibrary.Gson)
    }
}