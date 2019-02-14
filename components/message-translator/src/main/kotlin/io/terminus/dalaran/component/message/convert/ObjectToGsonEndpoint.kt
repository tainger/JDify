package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranEndpoint
import io.terminus.dalaran.NotConfigurableDalaranEndpoint
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

@DalaranComponent("object-to-gson")
class ObjectToGsonEndpoint : NotConfigurableDalaranEndpoint {
    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        route.marshal().json(JsonLibrary.Gson)
    }
}