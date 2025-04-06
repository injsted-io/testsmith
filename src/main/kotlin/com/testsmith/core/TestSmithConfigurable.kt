package com.testsmith.core

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class TestSmithConfigurable : Configurable {
    private val ui = SettingsUI()

    override fun getDisplayName(): String = "TestSmith"

    override fun createComponent(): JComponent = ui.component

    override fun isModified(): Boolean {
        val settings = ConfigService.getInstance()?.state ?: return false
        return ui.isModified(settings)
    }

    override fun apply() {
        val settings = ConfigService.getInstance()?.state ?: return
        ui.apply(settings)
    }

    override fun reset() {
        val settings = ConfigService.getInstance()?.state ?: return
        ui.reset(settings)
    }

    override fun getPreferredFocusedComponent(): JComponent? = ui.component
}
