package kg.optimabank.prototype.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    var items = mutableListOf<T>()

    open fun setData(data: List<T>, isUpdate: Boolean = false) {
        if (isUpdate) {
            items.clear()
            notifyItemRangeChanged(0, itemCount)
        }
        val startIndex = items.size
        items.addAll(data)
        notifyItemRangeInserted(startIndex, data.size)
    }


    override fun getItemCount(): Int = items.size
}