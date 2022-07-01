package kg.optimabank.prototype.features.department.list.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kg.optimabank.prototype.base.BaseListAdapter
import kg.optimabank.prototype.databinding.ItemDepartmentBinding
import kg.optimabank.prototype.features.department.dto.Department

class DepartmentsAdapter(context: Context) : BaseListAdapter<Department>(DiffCallback()) {

    private val inflater = LayoutInflater.from(context)
    private var clickAction: ((Department) -> Unit)? = null

    fun setClickAction(action: (Department) -> Unit) {
        this.clickAction = action
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding = ItemDepartmentBinding.inflate(inflater, parent, false)
        return DepartmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as DepartmentViewHolder).bind(items[position])
    }

    inner class DepartmentViewHolder(
        private val binding: ItemDepartmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(department: Department) {
            binding.apply {
                root.setOnClickListener {
                    clickAction?.invoke(department)
                }
                textCode.text = department.code
                textTitle.text = department.name
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Department>() {
        override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean {
            return (oldItem.bic == newItem.bic && oldItem.code == newItem.code &&
                    oldItem.level == newItem.level && oldItem.name == newItem.name &&
                    oldItem.parentСode == newItem.parentСode && oldItem.way4 == newItem.way4)
        }
    }
}