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

class ConvertToJson : AnAction() {

    private val gson = Gson()

    override fun actionPerformed(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        val psiClass = psiElement as PsiClass
        val templateData = getTemplateData(psiClass, hashSetOf(psiClass))
        CopyPasteManager.getInstance().setContents(StringSelection(gson.toJson(templateData)))
    }

    private fun getTemplateData(psiClass: PsiClass, usedClasses: HashSet<PsiClass>) = psiClass.allFields.filter {
        it.modifierList?.hasModifierProperty("static") != true
    }.map {
//        it.docComment?.descriptionElements?.map { it.text?.trim() }?.joinToString("")?.trim()
        it.name to getDefaultValue(it.type, usedClasses)
    }.toMap()

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement is PsiClass
    }

    private fun getDefaultValue(type: PsiType, usedClasses: HashSet<PsiClass>): Any? {
        var value = getBaseTypeValue(type)
        if (value != null) return value
        if (type !is PsiClassReferenceType) {
            return type.canonicalText
        }
        value = getEnumValue(type)
        if (value != null) return value
        value = getArrayValue(type, usedClasses)
        if (value != null) return value
        value = getCollectionValue(type, usedClasses)
        if (value != null) return value
        if (isMap(type)) {
            return emptyMap<String, Any>()
        }
        val resolveClass = type.resolve() ?: return null
        return if (!usedClasses.contains(resolveClass)) {
            usedClasses.add(resolveClass)
            getTemplateData(resolveClass, usedClasses)
        } else {
            return emptyMap<String, Any>()
        }
    }

    private fun isCollection(type: PsiType): Boolean {
        if (type !is PsiClassReferenceType) {
            return false
        }
        val resolveClass = type.resolve() ?: return false
        if (resolveClass.qualifiedName == COLLECTION) {
            return true
        }
        if (resolveClass.superTypes.isNotEmpty() && resolveClass.superTypes.any { isCollection(it) }) {
            return true
        }
        return false
    }

    private fun isMap(type: PsiType): Boolean {
        if (type !is PsiClassReferenceType) {
            return false
        }
        val resolveClass = type.resolve() ?: return false
        if (resolveClass.qualifiedName == MAP) {
            return true
        }
        if (resolveClass.superTypes.isNotEmpty() && resolveClass.superTypes.any { isMap(it) }) {
            return true
        }
        return false
    }

    private fun getCollectionValue(type: PsiType, usedClasses: HashSet<PsiClass>): Array<Any>? {
        if (!isCollection(type)) {
            return null
        }
        type as PsiClassReferenceType
        if (type.parameters.isNotEmpty()) {
            val value = getDefaultValue(type.parameters[0], usedClasses)
            if (value != null) {
                return arrayOf(value)
            }
        }
        return emptyArray()
    }

    private fun getArrayValue(type: PsiType, usedClasses: HashSet<PsiClass>): Array<Any>? {
        if (type !is PsiArrayType) {
            return null
        }
        val value = getDefaultValue(type.componentType, usedClasses)
        return if (value != null) {
            arrayOf(value)
        } else {
            emptyArray()
        }
    }

    private fun getEnumValue(type: PsiType): String? {
        if (type !is PsiClassReferenceType) {
            return null
        }
        val resolveClass = type.resolve() ?: return null
        if (resolveClass.isEnum) {
            return resolveClass.fields.first().name
        }
        return null
    }

    private fun getBaseTypeValue(type: PsiType): Any? {
        return when (type.canonicalText) {
            // 基础类型
            "boolean" -> true
            "char" -> "string"
            "byte" -> 1
            "short" -> 1
            "int" -> 1
            "long" -> 1
            "float" -> 1.0
            "double" -> 1.0
            // 包装基础类型
            "java.lang.Boolean" -> true
            "java.lang.Character" -> "string"
            "java.lang.Byte" -> 1
            "java.lang.Short" -> 1
            "java.lang.Integer" -> 1
            "java.lang.Long" -> 1
            "java.lang.Float" -> 1.0
            "java.lang.Double" -> 1.0
            // 经常使用的类
            "java.lang.String" -> "string"
            "java.math.BigDecimal" -> BigDecimal.ONE
            "java.util.Date" -> DateFormatUtil.formatDateTime(Date())
            else -> null
        }
    }
}