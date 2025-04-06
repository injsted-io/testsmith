Sure! Here's your nicely formatted `README.md` content:

---

# 🧪 TestSmith IntelliJ Plugin

**TestSmith** is an IntelliJ IDEA plugin that makes it seamless to generate unit tests and navigate between source and test files for frontend codebases.

---

## ✨ Features

### ✅ Generate Test

- Right-click any `.ts`, `.tsx`, `.js`, or `.jsx` file inside the `src/` directory.
- Automatically generates a test file in a mirrored path under `src/__tests__` (or your configured directory).
- Detects the test framework (`vitest` or `jest`) based on `package.json`.
- If the test file already exists, it opens the test file instead of regenerating it.
- Quick toast notification indicates success or if the file already existed.

### 🔁 Reverse Lookup (Test → Source)

- Right-click any test file to reveal the **"Open Source from Test"** option.
- Navigates to the corresponding source file by stripping `.test` from the file name.

### ⚙️ Plugin Settings

Navigate to `Preferences > TestSmith` to:

- Change the **Test Output Directory** (default is `src/__tests__`)
- Change the **Default Test Framework** (e.g. `vitest`, `jest`)

Settings are persisted across sessions using IntelliJ's `PersistentStateComponent`.

---

## 🛠️ Folder Overview

```
com/testsmith/core/
├── GenerateTestAction.kt       ← Handles test file generation
├── ReverseLookupAction.kt      ← Finds original source from test
├── ConfigService.kt            ← IntelliJ persistent plugin config
├── Config.kt                   ← Static config access helper
├── SettingsUI.kt               ← UI layout for the settings screen
├── TestSmithConfigurable.kt    ← Connects UI to IDE settings
└── TestGutterIconRenderer.kt   ← (Optional) Gutter icon renderer
```

---

## 🔧 Development Notes

- Config values are stored in `.idea/testsmithSettings.xml`
- Toasts auto-expire after 1 second (unless it's a warning or error)
- File creation uses `WriteCommandAction` to ensure IDE-safe file updates
- File system changes are refreshed with `VirtualFileManager.syncRefresh()`
- Files open using `FileEditorManager.getInstance(project).openFile(...)`

---

## 🚀 Run Locally

1. Open the project in **IntelliJ IDEA Ultimate**
2. Use Gradle task `runIde` to launch a sandbox environment
3. Right-click any frontend file in `src/` to try out the **Generate Test** action

---

## 🧼 Cleanup Tips

If you accidentally added files to version control:

```bash
git rm --cached -r .
echo "node_modules/\n.idea/\nout/\nbuild/\n.gradle/" >> .gitignore
git add .
git commit -m "cleanup: remove accidentally tracked files and add .gitignore"
```

---

Let me know if you want a logo, badges, or if you're planning to publish it to the Marketplace and want metadata too!