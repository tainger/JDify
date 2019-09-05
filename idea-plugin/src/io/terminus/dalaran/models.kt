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
        root: Field,
        val name: String?,
        val description: String?,
        val modelKey: String?,
        val modelType: String = "OBJECT"
) {
    val modelSchema = ModelSchema(root)
}

class ModelSchema(
        root: Field
) {
    val fields = mapOf(ROOT_FIELD to root)
}

class TriggerInfo(
        val id: String,
        val description: String?,
        val triggerType: String,
        val triggerConfig: Map<String, Any>,
        val inModel: ModelInfo?,
        val outModel: ModelInfo?
)

class ProcessorInfo(
        val processorType: String,
        val processorConfig: Map<String, Any>,
        val inModel: ModelInfo?,
        val outModel: ModelInfo?
)
