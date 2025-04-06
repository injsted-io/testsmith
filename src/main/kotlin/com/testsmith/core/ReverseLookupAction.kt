package com.testsmith.core

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.ActionUpdateThread
import java.nio.file.Paths

class ReverseLookupAction : AnAction("Open Source from Test") {

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val testFile = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val testFilePath = Paths.get(testFile.path)

        val basePath = Paths.get(project.basePath ?: return)
        val srcRoot = basePath.resolve("src")
        val testsRoot = basePath.resolve(Config.testRootDir)

        if (!testFilePath.startsWith(testsRoot)) {
            notify(project, "File is not inside ${Config.testRootDir}", NotificationType.WARNING)
            return
        }

        val relativeTestPath = testsRoot.relativize(testFilePath)
        val ext = testFilePath.toFile().extension
        val fileName = testFilePath.fileName.toString()

        if (!fileName.endsWith(".test.$ext")) {
            notify(project, "Not a test file (must end in .test.ts[x] or .test.js[x])", NotificationType.WARNING)
            return
        }

        val sourceFileName = fileName.replace(".test.$ext", ".$ext")
        val sourcePath = srcRoot.resolve(relativeTestPath).parent.resolve(sourceFileName)
        val sourceFile = sourcePath.toFile()

        if (sourceFile.exists()) {
            val vFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourceFile) ?: return
            FileEditorManager.getInstance(project).openFile(vFile, true)
        } else {
            notify(project, "Source file not found:\n→ ${sourcePath}", NotificationType.ERROR)
        }
    }

    private fun notify(project: Project, message: String, type: NotificationType) {
        val notification = Notification("testsmith", "TestSmith", message, type)
        Notifications.Bus.notify(notification, project)
    }
}
