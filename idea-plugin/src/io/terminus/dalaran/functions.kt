package io.terminus.dalaran

import com.google.gson.GsonBuilder
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import org.apache.commons.lang3.RandomStringUtils
import java.awt.datatransfer.StringSelection
import java.util.*

private val gson = GsonBuilder().apply {
    this.setPrettyPrinting()
}.create()

fun Any.setCopyPasteContent() {
    CopyPasteManager.getInstance().setContents(StringSelection(gson.toJson(this)))
}

fun ModelInfo.buildTemplateData(): Any {
    val rootField = this.modelSchema.fields[ROOT_FIELD] ?: return emptyMap<String, Any>()
    return rootField.getTemplateValue()
}

fun buildSchema(type: PsiType): ModelInfo {
    val rootField = buildField(type, hashSetOf())
    return ModelInfo(rootField, type.presentableText, type.canonicalText, type.canonicalText)
}

fun buildSchema(psiClass: PsiClass): ModelInfo {
    val usedClasses = hashSetOf<PsiClass>()
    val fields = psiClass.allFields.filter {
        it.modifierList?.hasModifierProperty("static") != true
    }.map { it.buildDalaranField(emptyMap(), usedClasses) }.toMap()
    val rootField = Field().apply {
        this.fields = fields
        this.type = FieldType.OBJECT
        this.description = "数据根节点"
    }
    val modelName = psiClass.docComment?.descriptionElements?.map { it.text?.trim() }?.joinToString("")?.trim()
            ?: psiClass.qualifiedName
    return ModelInfo(rootField, psiClass.name, modelName, psiClass.qualifiedName)
}

private fun Field.getTemplateValue(): Any {
    return when (this.type) {
        FieldType.STRING -> RandomStringUtils.randomAlphabetic(6)
        FieldType.BOOLEAN -> true
        FieldType.FLOAT -> 1.5
        FieldType.INTEGER -> 1
        FieldType.DATE -> Date()
        FieldType.ARRAY -> {
            Field().let {
                it.type = this.subType ?: FieldType.STRING
                it.fields = this.fields
                it
            }.getTemplateValue()
        }
        FieldType.OBJECT -> this.fields.map { it.key to it.value.getTemplateValue() }.toMap()
    }
}

private fun buildFieldsByType(type: PsiClassReferenceType, usedClasses: HashSet<PsiClass>): Map<String, Field> {
    val typeParameters = type.resolve()?.typeParameters ?: emptyArray()
    val classMapper = type.typeArguments().mapIndexed { i, refType ->
        if (i < typeParameters.size && refType is PsiClassReferenceType) {
            typeParameters[i].name!! to refType
        } else null
    }.filterNotNull().toMap()
    return type.resolve()!!.allFields.filter {
        it.modifierList?.hasModifierProperty("static") != true
    }.map { it.buildDalaranField(classMapper, usedClasses) }.toMap()
}

private fun PsiType.getReferenceType() = if (this is PsiClassReferenceType) this else null

private fun PsiField.buildDalaranField(classMapper: Map<String, PsiClassReferenceType>, usedClasses: HashSet<PsiClass>): Pair<String, Field> {
    val fieldType = classMapper[this.type.canonicalText] ?: this.type
    val field = buildField(fieldType, usedClasses)
    this.docComment?.descriptionElements?.map { it.text?.trim() }?.joinToString("")?.trim()?.let {
        field.description = it
    }
    field.nullable = !this.hasAnnotation("org.jetbrains.annotations.NotNull")
    return this.name to field
}

private fun buildField(type: PsiType, usedClasses: HashSet<PsiClass>): Field {
    val field = Field()
    field.type = when (type.canonicalText) {
        "byte", "short", "int", "long", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long" -> FieldType.INTEGER
        "float", "double", "java.lang.Float", "java.lang.Double", "java.math.BigDecimal" -> FieldType.FLOAT
        "boolean", "java.lang.Boolean" -> FieldType.BOOLEAN
        "char", "java.lang.Character", "java.lang.String" -> FieldType.STRING
        "java.util.Date" -> FieldType.DATE
        else -> when (type) {
            is PsiArrayType -> {
                type.componentType.getReferenceType()?.let {
                    field.fields = buildFieldsByType(it, usedClasses)
                }
                FieldType.ARRAY
            }
            is PsiClassReferenceType -> {
                val resolveClass = type.resolve()
                if (resolveClass == null) {
                    FieldType.STRING
                } else when {
                    resolveClass.isEnum -> FieldType.STRING
                    isCollection(resolveClass) -> {
                        val subField = buildField(type.parameters[0], usedClasses)
                        field.subType = subField.type
                        if (!subField.type.basicType) {
                            field.fields = subField.fields
                        }
                        FieldType.ARRAY
                    }
                    isMap(resolveClass) -> FieldType.OBJECT
                    else -> {
                        // 避免循环引用导致无限循环调用
                        if (!usedClasses.contains(resolveClass) && type is PsiClassReferenceType) {
                            usedClasses.add(resolveClass)
                            field.fields = buildFieldsByType(type, usedClasses)
                        }
                        FieldType.OBJECT
                    }
                }
            }
            else -> FieldType.STRING
        }
    }
    return field
}

private fun isCollection(resolveClass: PsiClass?): Boolean {
    resolveClass ?: return false
    if (resolveClass.qualifiedName == COLLECTION) {
        return true
    }
    if (resolveClass.superTypes.isNotEmpty() && resolveClass.superTypes.any { isCollection(it.resolve()) }) {
        return true
    }
    return false
}

private fun isMap(resolveClass: PsiClass?): Boolean {
    resolveClass ?: return false
    if (resolveClass.qualifiedName == MAP) {
        return true
    }
    if (resolveClass.superTypes.isNotEmpty() && resolveClass.superTypes.any { isMap(it.resolve()) }) {
        return true
    }
    return false
}
