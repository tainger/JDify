package io.terminus.dalaran.component.netty.http

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranListener

@DalaranComponent("netty-http-listener", configType = NettyHttpConfig::class)
class NettyHttpListener(
        private val config: NettyHttpConfig
) : DalaranListener {

    private val camelComponentScheme = "netty4-http"

    override fun getUri(properties: Map<String, String>): String =
            "$camelComponentScheme:${config.protocol}://${config.host}:${config.port}/${config.path}?httpMethodRestrict=${config.method}"
}