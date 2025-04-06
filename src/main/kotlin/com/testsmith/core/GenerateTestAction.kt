package com.testsmith.core

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class GenerateTestAction : AnAction("Generate Test") {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT // run update in background thread
    }

    override fun update(e: AnActionEvent) {
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE)
        e.presentation.isEnabledAndVisible =
            file != null && !file.isDirectory && file.extension in listOf("ts", "tsx", "js", "jsx")
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val filePath = file.path
        generateTest(project, filePath)
    }

    companion object {
        fun generateTest(project: Project, filePath: String) {
            if (filePath.contains(".test.")) {
                notify(project, "Skipped: Already a test file.", NotificationType.WARNING)
                return
            }

            val basePath = Paths.get(project.basePath ?: return)
            val srcRoot = basePath.resolve("src")
            val fullPath = Paths.get(filePath)

            if (!fullPath.startsWith(srcRoot)) {
                notify(project, "File must be inside src/", NotificationType.ERROR)
                return
            }

            val relativePath = srcRoot.relativize(fullPath)
            val ext = fullPath.toFile().extension

            if (ext !in listOf("ts", "tsx", "js", "jsx")) {
                notify(project, "Unsupported file type: .$ext", NotificationType.WARNING)
                return
            }

            val testFilePath = basePath
                .resolve(Config.testRootDir)
                .resolve(relativePath)
                .let {
                    it.parent.resolve(it.fileName.toString().replace(".$ext", ".test.$ext"))
                }

            val testFile = testFilePath.toFile()
            val importPath = "@/".plus(relativePath.toString().replace(File.separator, "/").removeSuffix(".$ext"))
            val importName = relativePath.fileName.toString().removeSuffix(".$ext")

            if (testFile.exists()) {
                openFile(project, testFile)
                notify(project, "ℹ️ Test already exists for $importName. Opened file.", NotificationType.INFORMATION, autoExpire = true)
                return
            }

            val framework = inferTestFramework(basePath) ?: Config.defaultTestFramework
            val boilerplate = when (framework) {
                "jest" -> """
                    import $importName from '$importPath'

                    describe('$importName', () => {
                        it('should work as expected', () => {
                            expect(true).toBe(true)
                        })
                    })
                """.trimIndent()
                else -> """
                    import { describe, it, expect } from '$framework'
                    import $importName from '$importPath'

                    describe('$importName', () => {
                        it('works as expected', () => {
                            expect(true).toBe(true)
                        })
                    })
                """.trimIndent()
            }

            WriteCommandAction.runWriteCommandAction(project) {
                testFile.parentFile.mkdirs()
                testFile.writeText(boilerplate)
                VirtualFileManager.getInstance().syncRefresh()
                openFile(project, testFile)
            }

            notify(project, "✅ Test created for $importName", NotificationType.INFORMATION, autoExpire = true)
        }

        private fun openFile(project: Project, file: File) {
            val vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file) ?: return
            ApplicationManager.getApplication()?.invokeLater {
                FileEditorManager.getInstance(project).openFile(vFile, true)
            }
        }

        private fun notify(
            project: Project,
            message: String,
            type: NotificationType,
            autoExpire: Boolean = false
        ) {
            val notification = Notification("testsmith", "TestSmith", message, type)

            Notifications.Bus.notify(notification, project)

            if (autoExpire && type != NotificationType.ERROR) {
                // Schedule expiration after 1 second
                javax.swing.Timer(1000) { notification.expire() }.apply {
                    isRepeats = false
                    start()
                }
            }
        }

        private fun inferTestFramework(basePath: Path): String? {
            val pkgJson = basePath.resolve("package.json").toFile()
            if (!pkgJson.exists()) return null
            val content = pkgJson.readText()
            return when {
                "vitest" in content -> "vitest"
                "jest" in content -> "jest"
                else -> null
            }
        }
    }
}
