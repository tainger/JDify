package io.terminus.dalaran.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import io.terminus.dalaran.buildSchema
import io.terminus.dalaran.buildTemplateData
import io.terminus.dalaran.setCopyPasteContent

class ConvertToJson : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val psiClass = e.getData(CommonDataKeys.PSI_ELEMENT) as PsiClass
        buildSchema(psiClass).buildTemplateData().setCopyPasteContent()
    }


    override fun update(e: AnActionEvent) {
        val psiElement = e.getData(CommonDataKeys.PSI_ELEMENT)
        e.presentation.isVisible = psiElement is PsiClass
    }
}