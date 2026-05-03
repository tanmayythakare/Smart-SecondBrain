$files = @(
    "frontend/secondbrain-frontend/src/app/features/notes/note-detail/note-detail.component.html",
    "frontend/secondbrain-frontend/src/app/features/notes/note-detail/note-detail.component.ts",
    "frontend/secondbrain-frontend/src/app/features/notes/note-detail/note-detail.component.css",
    "frontend/secondbrain-frontend/src/app/app.module.ts",
    "frontend/secondbrain-frontend/src/app/app-routing.module.ts"
)

$outputFile = "code2.txt"
if (Test-Path $outputFile) { Remove-Item $outputFile }

foreach ($file in $files) {
    if (Test-Path $file) {
        Add-Content $outputFile "`n`n// ========================================`n// FILE: $file`n// ========================================`n"
        Get-Content $file | Add-Content $outputFile
    } else {
        Add-Content $outputFile "`n`n// FILE NOT FOUND: $file"
    }
}
