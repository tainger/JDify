package io.terminus.dalaran

import com.google.gson.Gson
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.util.text.DateFormatUtil
import java.awt.datatransfer.StringSelection
import java.math.BigDecimal
import java.util.*

private const val COLLECTION = "java.util.Collection"
private const val MAP = "java.util.Map"

private enum class FieldType {
    STRING, INTEGER, FLOAT, DATE, BOOLEAN, ARRAY, OBJECT;
}

private class Field {
    var type: FieldType = FieldType.STRING
    var description: String = ""
    var nullable: Boolean = false
    var subType: FieldType? = null
    var fields: Map<String, Field> = hashMapOf()
}

private data class Model(
        val root: Field
)

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
    }.map {
        val comment = it.docComment?.descriptionElements?.map { it.text?.trim() }?.joinToString("")?.trim()
        it.name to buildField(it.type, comment, usedClasses)
    }.toMap()

    private fun getResolveClass(type: PsiType?): PsiClass? {
        if (type is PsiClassReferenceType) {
            return type.resolve()
        }
        return null
    }

    private fun buildField(type: PsiType, comment: String?, usedClasses: HashSet<PsiClass>): Field {
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
                        if (resolveClass.isEnum) {
                            FieldType.STRING
                        } else if (isCollection(resolveClass)) {
                            getResolveClass(type.parameters[0])?.let {
                                buildFields(it, usedClasses)
                            }?.let {
                                field.fields = it
                            }
                            FieldType.ARRAY
                        } else if (isMap(resolveClass)) {
                            FieldType.OBJECT
                        } else {
                            field.fields = buildFields(resolveClass, usedClasses)
                            FieldType.OBJECT
                        }
                    }
                }
                FieldType.STRING
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