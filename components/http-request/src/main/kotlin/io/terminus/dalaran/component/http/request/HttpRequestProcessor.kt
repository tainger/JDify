package io.terminus.dalaran.component.http.request

import io.terminus.dalaran.annotation.DalaranComponent
import io.terminus.dalaran.DalaranProcessor
import org.apache.camel.builder.Builder.constant
import org.apache.camel.model.RouteDefinition

@DalaranComponent("http-request", configType = HttpRequestConfig::class)
class HttpRequestProcessor : DalaranProcessor<HttpRequestConfig> {
    private val uri = "%s4://%s:%s%s?bridgeEndpoint=true"
    override fun configure(route: RouteDefinition, config: HttpRequestConfig) {
        val uri = String.format(uri, config.protocol.value, config.host, config.port, config.path)
        route.setHeader("CamelHttpMethod", constant(config.method)).to(uri)
    }
}