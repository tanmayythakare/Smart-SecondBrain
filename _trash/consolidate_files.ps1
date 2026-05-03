$files = @(
    "frontend/secondbrain-frontend/src/app/features/tasks/task-list/task-list.component.html",
    "frontend/secondbrain-frontend/src/app/features/notes/note-list/note-list.component.html",
    "frontend/secondbrain-frontend/src/app/features/ai-chat/ai-chat.component.html",
    "frontend/secondbrain-frontend/src/app/features/auth/login/login.component.html",
    "frontend/secondbrain-frontend/src/app/features/auth/register/register.component.html",
    "frontend/secondbrain-frontend/src/app/app.component.html",
    
    "frontend/secondbrain-frontend/src/app/features/tasks/task-list/task-list.component.ts",
    "frontend/secondbrain-frontend/src/app/features/notes/note-list/note-list.component.ts",
    "frontend/secondbrain-frontend/src/app/features/ai-chat/ai-chat.component.ts",
    "frontend/secondbrain-frontend/src/app/features/auth/login/login.component.ts",
    "frontend/secondbrain-frontend/src/app/features/auth/register/register.component.ts",
    "frontend/secondbrain-frontend/src/app/app.component.ts",
    
    "frontend/secondbrain-frontend/src/app/features/tasks/task-list/task-list.component.css",
    "frontend/secondbrain-frontend/src/app/features/notes/note-list/note-list.component.css",
    "frontend/secondbrain-frontend/src/app/features/ai-chat/ai-chat.component.css",
    "frontend/secondbrain-frontend/src/app/features/auth/login/login.component.css",
    "frontend/secondbrain-frontend/src/app/app.component.css",
    "frontend/secondbrain-frontend/src/styles.css",
    
    "frontend/secondbrain-frontend/src/app/features/tasks/task.service.ts",
    "frontend/secondbrain-frontend/src/app/features/notes/note.service.ts",
    "frontend/secondbrain-frontend/src/app/features/ai-chat/ai-chat.service.ts",
    "frontend/secondbrain-frontend/src/app/core/interceptors/jwt.interceptor.ts",
    "frontend/secondbrain-frontend/src/app/features/auth/auth.service.ts",
    
    "backend/src/main/java/com/example/backend/model/Task.java",
    "backend/src/main/java/com/example/backend/model/Note.java",
    "backend/src/main/java/com/example/backend/service/TaskService.java",
    "backend/src/main/java/com/example/backend/service/NoteService.java",
    "backend/src/main/java/com/example/backend/repository/TaskRepository.java",
    "backend/src/main/java/com/example/backend/repository/NoteRepository.java",
    "backend/src/main/java/com/example/backend/security/SecurityConfig.java",
    "backend/src/main/resources/application.properties",
    
    "frontend/secondbrain-frontend/angular.json",
    "frontend/secondbrain-frontend/package.json",
    "frontend/secondbrain-frontend/src/environments/environment.ts",
    "frontend/secondbrain-frontend/src/environments/environment.prod.ts"
)

$outputFile = "code.txt"
if (Test-Path $outputFile) { Remove-Item $outputFile }

foreach ($file in $files) {
    if (Test-Path $file) {
        Add-Content $outputFile "`n`n// ========================================`n// FILE: $file`n// ========================================`n"
        Get-Content $file | Add-Content $outputFile
    } else {
        Add-Content $outputFile "`n`n// FILE NOT FOUND: $file"
    }
}
