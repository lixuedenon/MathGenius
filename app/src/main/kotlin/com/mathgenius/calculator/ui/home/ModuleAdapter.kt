// app/src/main/kotlin/com/mathgenius/calculator/ui/home/ModuleAdapter.kt
package com.mathgenius.calculator.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mathgenius.calculator.R

/**
 * 模块列表适配器
 * 用于在 RecyclerView 中显示数学模块卡片
 *
 * @property modules 模块数据列表
 * @property onModuleClick 模块点击回调函数
 */
class ModuleAdapter(
    private val modules: List<ModuleData>,
    private val onModuleClick: (ModuleData) -> Unit
) : RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

    /**
     * ViewHolder 内部类
     * 持有并绑定单个模块卡片的视图
     */
    inner class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgIcon: ImageView = itemView.findViewById(R.id.img_module_icon)
        val txtName: TextView = itemView.findViewById(R.id.txt_module_name)
        val txtDescription: TextView = itemView.findViewById(R.id.txt_module_description)
        val imgArrow: ImageView = itemView.findViewById(R.id.img_arrow)

        /**
         * 绑定模块数据到视图
         *
         * @param module 模块数据对象
         */
        fun bind(module: ModuleData) {
            imgIcon.setImageResource(module.iconResId)
            txtName.setText(module.nameResId)
            txtDescription.setText(module.descriptionResId)

            imgIcon.setColorFilter(
                itemView.context.getColor(module.colorResId)
            )

            itemView.setOnClickListener {
                if (module.isAvailable) {
                    onModuleClick(module)
                } else {
                    Toast.makeText(
                        itemView.context,
                        R.string.module_coming_soon,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            itemView.alpha = if (module.isAvailable) 1.0f else 0.5f
            imgArrow.visibility = if (module.isAvailable) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_module_card, parent, false)
        return ModuleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
        holder.bind(modules[position])
    }

    override fun getItemCount() = modules.size
}