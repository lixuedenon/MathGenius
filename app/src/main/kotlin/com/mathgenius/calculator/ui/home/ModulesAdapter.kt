// app/src/main/kotlin/com/mathgenius/calculator/ui/home/ModulesAdapter.kt
// Kotlin Source File

package com.mathgenius.calculator.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mathgenius.calculator.R

/**
 * 数学模块列表适配器
 *
 * 功能：
 * - 展示所有数学模块
 * - 处理模块点击事件
 * - 区分已启用和未启用模块
 */
class ModulesAdapter(
    private val modules: List<HomeActivity.MathModule>,
    private val onModuleClick: (HomeActivity.MathModule) -> Unit
) : RecyclerView.Adapter<ModulesAdapter.ModuleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_card, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount(): Int = modules.size

    inner class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val card: MaterialCardView = itemView.findViewById(R.id.card_module)
        private val icon: ImageView = itemView.findViewById(R.id.iv_module_icon)
        private val name: TextView = itemView.findViewById(R.id.tv_module_name)
        private val desc: TextView = itemView.findViewById(R.id.tv_module_desc)

        fun bind(module: HomeActivity.MathModule) {
            name.text = itemView.context.getString(module.nameResId)
            desc.text = itemView.context.getString(module.descResId)

            // 设置图标，如果资源不存在则使用默认图标
            try {
                icon.setImageResource(module.iconResId)
            } catch (e: Exception) {
                icon.setImageResource(android.R.drawable.ic_menu_help)
            }

            card.alpha = if (module.enabled) 1.0f else 0.5f

            card.setOnClickListener {
                onModuleClick(module)
            }
        }
    }
}