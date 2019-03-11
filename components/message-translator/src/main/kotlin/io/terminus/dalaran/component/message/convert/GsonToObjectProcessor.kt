package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.annotation.DalaranComponent
import io.terminus.dalaran.DalaranProcessor
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

@DalaranComponent("gson-to-object", configType = GsonToObjectConfig::class)
class GsonToObjectProcessor : DalaranProcessor<GsonToObjectConfig> {
    override fun configure(route: RouteDefinition, config: GsonToObjectConfig) {
        val unmarshalType = Class.forName(config.targetType)
        route.unmarshal().json(JsonLibrary.Gson, unmarshalType)
    }
}