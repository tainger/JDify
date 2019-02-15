package io.terminus.dalaran.component.message.convert

class MessageConvertConfig(
        val targetModel: String,
        val mappingFile: String,
        val mapping: Map<String, String>
)