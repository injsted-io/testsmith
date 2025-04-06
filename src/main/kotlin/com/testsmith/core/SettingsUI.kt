package com.testsmith.core

import com.intellij.openapi.options.ConfigurableUi
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent
import javax.swing.JPanel

class SettingsUI : ConfigurableUi<ConfigService.State> {
    private var testOutputField: Cell<ExpandableTextField>? = null
    private var testFrameworkField: Cell<ExpandableTextField>? = null
    private lateinit var panel: JPanel

    override fun reset(settings: ConfigService.State) {
        testOutputField?.component?.text = settings.testRootDir
        testFrameworkField?.component?.text = settings.defaultTestFramework
    }

    override fun isModified(settings: ConfigService.State): Boolean {
        return testOutputField?.component?.text != settings.testRootDir ||
                testFrameworkField?.component?.text != settings.defaultTestFramework
    }

    override fun apply(settings: ConfigService.State) {
        settings.testRootDir = testOutputField?.component?.text ?: ""
        settings.defaultTestFramework = testFrameworkField?.component?.text ?: ""
    }

    override fun getComponent(): JComponent {
        panel = panel {
            group("TestSmith Plugin Settings") {
                row("Test Output Directory (relative to project root):") {
                    testOutputField = expandableTextField()
                        .comment("Example: src/__tests__")
                        .align(Align.FILL)
                }

                row("Default Test Framework (vitest, jest, etc):") {
                    testFrameworkField = expandableTextField()
                        .comment("Used to determine test syntax and boilerplate")
                        .align(Align.FILL)
                }
            }
        }
        return panel
    }
}
