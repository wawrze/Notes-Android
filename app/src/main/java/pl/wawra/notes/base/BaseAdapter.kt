package pl.wawra.notes.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<ITEM, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    open var data: List<ITEM> = ArrayList()
        set(items) {
            field = items
            notifyDataSetChanged()
        }

    private fun getItem(position: Int): ITEM = data[position]

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, getItem(position))
    }

    protected abstract fun onBindViewHolder(holder: VH, item: ITEM)

    override fun getItemCount() = data.size

    protected fun inflate(parent: ViewGroup, @LayoutRes res: Int): View {
        return LayoutInflater.from(parent.context).inflate(res, parent, false)
    }

}