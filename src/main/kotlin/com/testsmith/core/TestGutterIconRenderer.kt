package com.testsmith.core

import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.FunctionUtil
import javax.swing.Icon
import com.intellij.openapi.util.IconLoader

class TestGutterIconRenderer(private val project: Project, private val element: PsiElement) :
    GutterIconRenderer() {

    override fun getIcon(): Icon = IconLoader.getIcon("/icons/testIcon.svg", javaClass)

    override fun getTooltipText(): String = "Generate test for this file"

    override fun equals(other: Any?): Boolean = other is TestGutterIconRenderer

    override fun hashCode(): Int = element.hashCode()
}
