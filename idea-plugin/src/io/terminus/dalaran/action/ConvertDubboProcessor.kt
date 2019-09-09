package io.terminus.dalaran.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.PsiClassReferenceType
import io.terminus.dalaran.DUBBO_PROVIDER_TYPE
import io.terminus.dalaran.ProcessorInfo
import io.terminus.dalaran.buildSchema
import io.terminus.dalaran.setCopyPasteContent

class ConvertDubboProcessor : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val method = e.getData(CommonDataKeys.PSI_ELEMENT) as PsiMethod
        val processorConfig = mapOf(
                "serviceId" to method.containingClass!!.qualifiedName!!,
                "method" to method.name
                // TODO version
        )
        val processorId = "${method.containingClass!!.qualifiedName!!}#${method.name}"
        val inModel = method.parameters.firstOrNull()?.let { buildSchema(it.type as PsiClassReferenceType) }
        val outModel = method.returnType?.let { buildSchema(it) }
        ProcessorInfo(DUBBO_PROVIDER_TYPE, processorConfig, inModel, outModel).setCopyPasteContent()
    }

    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement is PsiMethod && psiElement.parent is PsiClass && (psiElement.parent as PsiClass).isInterface
    }
}