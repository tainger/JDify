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

class ModelSchema(
        root: Field,
        val modelType: String = "OBJECT"
) {
    val fields = mapOf(ROOT_FIELD to root)
}

class TriggerInfo(
        val id: String,
        val triggerType: String,
        val triggerConfig: Map<String, Any>,
        val inModel: ModelSchema?,
        val outModel: ModelSchema?
)

class ProcessorInfo(
        val processorType: String,
        val processorConfig: Map<String, Any>,
        val inModel: ModelSchema?,
        val outModel: ModelSchema?
)
