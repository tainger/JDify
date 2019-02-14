package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranEndpoint
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

class GsonToObjectEndpoint(
        private val targetType: String
) : DalaranEndpoint {
    override fun getType() = "gson-to-object"

    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        val unmarshalType = Class.forName(targetType)
        route.unmarshal().json(JsonLibrary.Gson, unmarshalType)
    }
}