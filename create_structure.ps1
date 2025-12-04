# MathGenius 项目结构创建脚本 - 简化版

Write-Host "🚀 开始创建 MathGenius 项目结构..." -ForegroundColor Green

$basePath = "app\src\main"
$kotlinBase = "$basePath\kotlin\com\mathgenius\calculator"
$resBase = "$basePath\res"

# 1. 创建目录结构
Write-Host "`n📁 创建目录..." -ForegroundColor Cyan

$dirs = @(
    "$kotlinBase\core\engine",
    "$kotlinBase\core\steps",
    "$kotlinBase\core\rules",
    "$kotlinBase\core\i18n",
    "$kotlinBase\modules\calculus\derivative",
    "$kotlinBase\modules\calculus\integral",
    "$kotlinBase\modules\linearalgebra\matrix",
    "$kotlinBase\modules\statistics",
    "$kotlinBase\modules\diffeq",
    "$kotlinBase\modules\discrete",
    "$kotlinBase\ui\common",
    "$kotlinBase\ui\home",
    "$kotlinBase\ui\theme",
    "$kotlinBase\visualization",
    "$kotlinBase\export",
    "$kotlinBase\plugins",
    "$resBase\values",
    "$resBase\values-zh",
    "$resBase\values-ja",
    "$resBase\values-ko",
    "$resBase\values-fr",
    "$resBase\values-de",
    "$resBase\values-es",
    "$resBase\layout",
    "$resBase\drawable"
)

foreach ($dir in $dirs) {
    New-Item -ItemType Directory -Force -Path $dir | Out-Null
    Write-Host "  + $dir" -ForegroundColor Gray
}

# 2. 创建空的 Kotlin 文件（稍后手动填充内容）
Write-Host "`n📄 创建 Kotlin 文件..." -ForegroundColor Cyan

$kotlinFiles = @(
    "$kotlinBase\core\engine\MathEngine.kt",
    "$kotlinBase\core\engine\ExpressionTree.kt",
    "$kotlinBase\core\engine\ExpressionParser.kt",
    "$kotlinBase\core\engine\Simplifier.kt",
    "$kotlinBase\core\engine\ComputationResult.kt",
    "$kotlinBase\core\steps\StepType.kt",
    "$kotlinBase\core\steps\CalculationStep.kt",
    "$kotlinBase\core\steps\StepTracker.kt",
    "$kotlinBase\core\rules\Rule.kt",
    "$kotlinBase\core\rules\RuleRegistry.kt",
    "$kotlinBase\core\i18n\LanguageManager.kt",
    "$kotlinBase\core\i18n\MathFormatter.kt",
    "$kotlinBase\modules\calculus\derivative\DerivativeEngine.kt",
    "$kotlinBase\ui\theme\ThemeManager.kt"
)

foreach ($file in $kotlinFiles) {
    New-Item -ItemType File -Force -Path $file | Out-Null
    Write-Host "  + $(Split-Path $file -Leaf)" -ForegroundColor Gray
}

# 3. 创建多语言资源文件
Write-Host "`n🌐 创建资源文件..." -ForegroundColor Cyan

# 英语
@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">MathGenius</string>
    <string name="module_calculus">Calculus</string>
    <string name="btn_calculate">Calculate</string>
</resources>
"@ | Out-File -FilePath "$resBase\values\strings.xml" -Encoding UTF8
Write-Host "  + values/strings.xml" -ForegroundColor Gray

# 中文
@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">数学天才</string>
    <string name="module_calculus">微积分</string>
    <string name="btn_calculate">计算</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-zh\strings.xml" -Encoding UTF8
Write-Host "  + values-zh/strings.xml" -ForegroundColor Gray

# 日语
@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">数学の天才</string>
    <string name="module_calculus">微積分</string>
    <string name="btn_calculate">計算</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-ja\strings.xml" -Encoding UTF8
Write-Host "  + values-ja/strings.xml" -ForegroundColor Gray

# 其他语言（简化版）
@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">수학 천재</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-ko\strings.xml" -Encoding UTF8

@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Génie Mathématique</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-fr\strings.xml" -Encoding UTF8

@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Mathe-Genie</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-de\strings.xml" -Encoding UTF8

@"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Genio Matemático</string>
</resources>
"@ | Out-File -FilePath "$resBase\values-es\strings.xml" -Encoding UTF8

Write-Host "`n✅ 目录和文件创建完成！" -ForegroundColor Green
Write-Host "`n📋 接下来的步骤：" -ForegroundColor Yellow
Write-Host "  1. 在 Android Studio 中刷新项目" -ForegroundColor White
Write-Host "  2. 我会为你提供每个 Kotlin 文件的完整代码" -ForegroundColor White
Write-Host "`n💡 提示：现在可以在 Android Studio 中看到新的目录结构了！" -ForegroundColor Cyan