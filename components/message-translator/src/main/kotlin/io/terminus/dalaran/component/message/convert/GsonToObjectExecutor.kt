package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranExecutor
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.dataformat.JsonLibrary

@DalaranComponent("gson-to-object", configType = GsonToObjectConfig::class)
class GsonToObjectExecutor : DalaranExecutor<GsonToObjectConfig> {
    override fun configure(route: RouteDefinition, properties: Map<String, String>, config: GsonToObjectConfig) {
        val unmarshalType = Class.forName(config.targetType)
        route.unmarshal().json(JsonLibrary.Gson, unmarshalType)
    }
}