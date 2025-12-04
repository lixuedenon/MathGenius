// app/src/main/kotlin/com/mathgenius/calculator/ui/home/ModuleData.kt
package com.mathgenius.calculator.ui.home

/**
 * 数学模块数据类
 * 用于在主界面显示不同的数学功能模块
 *
 * @property id 模块唯一标识符，用于区分不同模块
 * @property nameResId 模块名称的字符串资源 ID
 * @property descriptionResId 模块描述的字符串资源 ID
 * @property iconResId 模块图标的 drawable 资源 ID
 * @property colorResId 模块主题色的 color 资源 ID
 * @property isAvailable 模块是否可用（true=已开发完成，false=待开发）
 */
data class ModuleData(
    val id: String,
    val nameResId: Int,
    val descriptionResId: Int,
    val iconResId: Int,
    val colorResId: Int,
    val isAvailable: Boolean
)