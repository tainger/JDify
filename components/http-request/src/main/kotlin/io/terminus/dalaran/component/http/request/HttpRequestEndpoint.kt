package io.terminus.dalaran.component.http.request

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranEndpoint
import io.terminus.dalaran.DalaranPropertyUtils
import org.apache.camel.builder.Builder.constant
import org.apache.camel.model.RouteDefinition

@DalaranComponent("http-request", configType = HttpRequestConfig::class)
class HttpRequestEndpoint(
        private val config: HttpRequestConfig
) : DalaranEndpoint {
    private val uri = "%s4://%s:%s%s?bridgeEndpoint=true"
    override fun configure(route: RouteDefinition, properties: Map<String, String>) {
        val uri = DalaranPropertyUtils.uriFormat(uri, properties, config.protocol.value, config.host, config.port, config.path)
        route.setHeader("CamelHttpMethod", constant(config.method)).to(uri)
    }
}