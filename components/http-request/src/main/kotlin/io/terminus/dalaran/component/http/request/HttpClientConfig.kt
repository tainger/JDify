package io.terminus.dalaran.component.http.request

import io.terminus.dalaran.annotation.DalaranConfigField
import io.terminus.dalaran.annotation.FieldInputType

class HttpClientConfig(
        @DalaranConfigField(label = "协议", inputType = FieldInputType.Radio)
        val protocol: HttpRequestProtocol,
        val host: String,
        val port: String,
        val path: String,
        val method: String
)