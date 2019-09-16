package io.terminus.dalaran.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import io.terminus.dalaran.*

class ConvertRestTriggerDubboFlow : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as PsiMethod
        val triggerConfig = mapOf(
                "protocol" to "HTTP",
                "method" to "POST",
                "path" to "/${method.containingClass!!.qualifiedName!!}"
        )
        val processorConfig = mapOf(
                "serviceId" to method.containingClass!!.qualifiedName!!,
                "method" to method.name,
                "version" to "1.0.0"
        )
        val triggerId = "${method.containingClass!!.qualifiedName!!}#${method.name}"
        val processorInModel = method.parameters.firstOrNull()?.let { buildSchema(it.type as PsiClassReferenceType) }
        val processorOutModel = method.returnType?.let { buildSchema(it) }

        val desc = method.docComment?.descriptionElements?.map { it.text?.trim() }?.joinToString("\n")?.trim()
        val inModel = processorInModel?.let {
            ModelInfo(it.modelSchema, "${it.name}-JSON", "${it.description}-JSON", "${it.modelKey}-JSON", "JSON")
        }
        val outModel = processorOutModel?.let {
            ModelInfo(it.modelSchema, "${it.name}-JSON", "${it.description}-JSON", "${it.modelKey}-JSON", "JSON")
        }
        ProcessorTriggerInfo(triggerId, desc, REST_TRIGGER_TYPE, triggerConfig, inModel, outModel, DUBBO_PROVIDER_TYPE, processorConfig, processorInModel, processorOutModel).setCopyPasteContent()
    }

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement is PsiMethod && psiElement.parent is PsiClass && (psiElement.parent as PsiClass).isInterface
    }

}
