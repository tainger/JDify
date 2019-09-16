package io.terminus.dalaran

enum class FieldType(
        val basicType: Boolean = true
) {
    STRING, INTEGER, FLOAT, DATE, BOOLEAN, ARRAY(false), OBJECT(false);
}

class Field {
    var type: FieldType = FieldType.STRING
    var nullable: Boolean = false
    var description: String? = null
    var subType: FieldType? = null
    var fields: Map<String, Field> = emptyMap()
}

class ModelInfo(
        val modelSchema: ModelSchema,
        val name: String?,
        val description: String?,
        val modelKey: String?,
        val modelType: String = "OBJECT"
)

class ModelSchema(
        root: Field
) {
    val fields = mapOf(ROOT_FIELD to root)
}

open class TriggerInfo(
        val name: String,
        val description: String?,
        val triggerType: String,
        val triggerConfig: Map<String, Any>,
        val inModel: ModelInfo?,
        val outModel: ModelInfo?
)

class ProcessorTriggerInfo(
        val name: String,
        val description: String?,
        val triggerType: String,
        val triggerConfig: Map<String, Any>,
        val inModel: ModelInfo?,
        val outModel: ModelInfo?,
        val processorType: String?,
        val processorConfig: Map<String, Any>,
        val processorInModel: ModelInfo?,
        val processorOutModel: ModelInfo?
)

class ProcessorInfo(
        val processorType: String,
        val processorConfig: Map<String, Any>,
        val inModel: ModelInfo?,
        val outModel: ModelInfo?
)
