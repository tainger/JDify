package io.terminus.dalaran.component.netty.http

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranTrigger

@DalaranComponent("netty-http-listener", configType = NettyHttpConfig::class)
class NettyHttpListener : DalaranTrigger<NettyHttpConfig> {
    private val camelComponentScheme = "netty4-http"

    override fun buildRouterUri(config: NettyHttpConfig): String =
            "$camelComponentScheme:${config.protocol.value}://${config.host}:${config.port}${config.path}?httpMethodRestrict=${config.method}"
}