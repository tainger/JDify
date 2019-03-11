package io.terminus.dalaran.component.dubbo.provider

class DubboProviderConfig(
        val registryAddress: String,
        val serviceId: String,
        val method: String,
        val version: String
)