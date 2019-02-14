package io.terminus.dalaran.component.dubbo.provider

import io.terminus.dalaran.DalaranComponent
import io.terminus.dalaran.DalaranListener

@DalaranComponent("dubbo-provider", configType = DubboProviderConfig::class)
class DubboProviderListener : DalaranListener<DubboProviderConfig> {
    override fun getUri(properties: MutableMap<String, String>, config: DubboProviderConfig) =
            "dubbo:?registryAddress=${config.registryAddress}&serviceId=${config.serviceId}&method=${config.method}&version=${config.version}"
}