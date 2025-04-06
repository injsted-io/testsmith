package com.testsmith.core

object Config {
    private val service: ConfigService?
        get() = ConfigService.getInstance()

    val testRootDir: String
        get() = service?.state?.testRootDir ?: "src/__tests__"

    val defaultTestFramework: String
        get() = service?.state?.defaultTestFramework ?: "vitest"

    fun setTestRootDir(path: String) {
        service?.state?.testRootDir = path
    }

    fun setDefaultTestFramework(framework: String) {
        service?.state?.defaultTestFramework = framework
    }
}

