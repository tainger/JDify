package io.terminus.dalaran

import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.util.text.DateFormatUtil
import java.awt.datatransfer.StringSelection
import java.math.BigDecimal
import java.util.*

private const val COLLECTION = "java.util.Collection"
private const val MAP = "java.util.Map"

private enum class FieldType(
        val basicType: Boolean = true
) {
    STRING, INTEGER, FLOAT, DATE, BOOLEAN, ARRAY(false), OBJECT(false);
}

private class Field {
    var type: FieldType = FieldType.STRING
    var nullable: Boolean = false
    var description: String? = null
    var subType: FieldType? = null
    var fields: Map<String, Field> = emptyMap()
}

private class Model(
        root: Field
) {
    val fields = mapOf("root" to root)
}

class ConvertToSchema : AnAction() {

    private val gson = Gson()

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        val psiClass = psiElement as PsiClass
        val fields = buildFields(psiClass, hashSetOf(psiClass))
        val rootField = Field().apply {
            this.fields = fields
            this.type = FieldType.OBJECT
            this.description = "数据根节点"
        }
        val model = Model(rootField)
        CopyPasteManager.getInstance().setContents(StringSelection(gson.toJson(model)))
    }

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement is PsiClass
    }

    private fun buildFields(psiClass: PsiClass, usedClasses: HashSet<PsiClass>) = psiClass.allFields.filter {
        it.modifierList?.hasModifierProperty("static") != true
    }.map { it.buildDalaranField(usedClasses) }.toMap()

    private fun getResolveClass(type: PsiType?): PsiClass? {
        if (type is PsiClassReferenceType) {
            return type.resolve()
        }
        return null
    }

    private fun PsiField.buildDalaranField(usedClasses: HashSet<PsiClass>): Pair<String, Field> {
        val field = buildField(this.type, usedClasses)
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
            else -> {
                if (type is PsiArrayType) {
                    getResolveClass(type.componentType)?.let {
                        buildFields(it, usedClasses)
                    }?.let {
                        field.fields = it
                    }
                    FieldType.ARRAY
                } else if (type is PsiClassReferenceType) {
                    val resolveClass = type.resolve()
                    if (resolveClass != null) {
                        when {
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
                                if (!usedClasses.contains(resolveClass)) {
                                    usedClasses.add(resolveClass)
                                    field.fields = buildFields(resolveClass, usedClasses)
                                }
                                FieldType.OBJECT
                            }
                        }
                    } else {
                        FieldType.STRING
                    }
                } else {
                    FieldType.STRING
                }
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

}