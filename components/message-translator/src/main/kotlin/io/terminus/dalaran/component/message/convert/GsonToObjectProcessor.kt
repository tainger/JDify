package io.terminus.dalaran.component.message.convert

import io.terminus.dalaran.DalaranProcessor
import io.terminus.dalaran.annotation.DalaranComponent
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.dataformat.JsonLibrary

@DalaranComponent("gson-to-object", configType = GsonToObjectConfig::class)
class GsonToObjectProcessor : DalaranProcessor<GsonToObjectConfig> {
    override fun configure(route: ProcessorDefinition, config: GsonToObjectConfig) {
        val unmarshalType = Class.forName(config.targetType)
        route.unmarshal().json(JsonLibrary.Gson, unmarshalType)
    }
}