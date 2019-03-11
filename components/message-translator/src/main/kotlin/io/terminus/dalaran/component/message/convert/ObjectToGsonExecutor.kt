package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.annotation.DalaranComponent
import io.terminus.dalaran.UnconfigurableDalaranExecutor
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

@DalaranComponent("object-to-gson")
class ObjectToGsonExecutor : UnconfigurableDalaranExecutor {
    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        route.marshal().json(JsonLibrary.Gson)
    }
}