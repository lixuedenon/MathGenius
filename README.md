# MathGenius - 数学天才计算器

## 项目简介

MathGenius 是一个功能强大的数学计算器应用，专为 K11 到大学三年级的学生设计。它不仅能计算结果，还能展示详细的解题步骤和说明。

## 主要特性

- ✅ **多模块设计**: 微积分、线性代数、概率统计、微分方程、离散数学
- ✅ **详细步骤显示**: 展示完整的解题过程和说明
- ✅ **多语言支持**: 支持 7 种语言（英、中、日、韩、法、德、西）
- ✅ **智能化简**: 自动化简复杂表达式
- ✅ **主题切换**: 支持亮色、暗色、护眼模式

## 当前完成度

- ✅ 核心引擎（100%）
- ✅ 微分模块（100%）
- ✅ 多语言资源（100%）
- ⏳ UI 界面（简单版完成）
- ⏳ 其他数学模块（待开发）

## 技术栈

- **语言**: Kotlin
- **最低 SDK**: API 24 (Android 7.0)
- **目标 SDK**: API 34 (Android 14)
- **架构**: MVVM + 三层架构
- **构建工具**: Gradle 8.0+

## 快速开始

### 1. 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17 或更高版本
- Android SDK 34

### 2. 克隆项目
```bash
git clone <repository-url>
cd MathGenius
```

### 3. 同步项目

在 Android Studio 中打开项目，等待 Gradle 同步完成。

### 4. 运行应用

点击运行按钮或使用快捷键：
- Windows/Linux: `Shift + F10`
- macOS: `Control + R`

## 使用示例

### 计算导数
```kotlin
val languageManager = LanguageManager(context)
val engine = DerivativeEngine(languageManager)
val result = engine.compute("x^2 + 2*x + 1")

println(result.formattedResult)  // 输出: 2*x + 2
result.steps.forEach { step ->
    println(step.getLocalizedExplanation(languageManager))
}
```

### 支持的表达式

- 基本运算: `x + 1`, `2 * x`, `x / 2`, `x - 1`
- 幂运算: `x^2`, `x^3`
- 三角函数: `sin(x)`, `cos(x)`, `tan(x)`
- 对数: `ln(x)`, `log(x)`
- 指数: `exp(x)`

## 项目结构
```
MathGenius/
├── app/
│   └── src/
│       └── main/
│           ├── kotlin/
│           │   └── com/mathgenius/calculator/
│           │       ├── core/           # 核心引擎
│           │       ├── modules/        # 功能模块
│           │       └── ui/             # 用户界面
│           └── res/                    # 资源文件
├── build.gradle.kts
└── README.md
```

## 开发路线图

### Phase 1 - 核心功能 ✅
- [x] 表达式解析器
- [x] 代数化简器
- [x] 微分引擎
- [x] 多语言支持
- [x] 基础 UI

### Phase 2 - 积分模块 ⏳
- [ ] 积分引擎
- [ ] 积分规则
- [ ] 积分 UI

### Phase 3 - 其他模块 ⏳
- [ ] 线性代数
- [ ] 概率统计
- [ ] 微分方程

### Phase 4 - 增强功能 ⏳
- [ ] 3D 可视化
- [ ] 导出功能
- [ ] 历史记录

## 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

项目链接: [https://github.com/yourusername/MathGenius](https://github.com/yourusername/MathGenius)

## 致谢

- 感谢所有贡献者
- 参考了 Wolfram Alpha 和 Symbolab 的步骤展示方式