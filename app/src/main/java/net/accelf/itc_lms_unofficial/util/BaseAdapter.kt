package net.accelf.itc_lms_unofficial.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, B : ViewBinding>(val items: List<T>, val bindingClass: Class<B>) :
    RecyclerView.Adapter<BaseAdapter<T, B>.ViewHolder<B>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B> {
        val binding = bindingClass
            .getMethod("inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.java)
            .invoke(null, LayoutInflater.from(parent.context), parent, false)
        @Suppress("UNCHECKED_CAST")
        return ViewHolder(binding as B)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)
}
