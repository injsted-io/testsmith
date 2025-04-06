package com.testsmith.core

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.application.ApplicationManager

@State(name = "TestSmithSettings", storages = [Storage("testsmithSettings.xml")])
@Service(Service.Level.APP)
class ConfigService : PersistentStateComponent<ConfigService.State> {

    data class State(
        var testRootDir: String = "src/__tests__",
        var defaultTestFramework: String = "vitest"
    )

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): ConfigService? =
            ApplicationManager.getApplication()?.getService(ConfigService::class.java)
    }

}
