package io.terminus.dalaran.component.http.request

class HttpClientConfig(
        val protocol: HttpRequestProtocol,
        val host: String,
        val port: String,
        val path: String,
        val method: String
)