package io.terminus.dalaran.component.netty.http

class NettyHttpConfig(
        val protocol: NettyHttpProtocol,
        val host: String,
        val port: Int,
        val path: String,
        val method: String
)