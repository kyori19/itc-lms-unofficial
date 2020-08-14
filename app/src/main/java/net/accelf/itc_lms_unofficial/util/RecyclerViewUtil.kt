package net.accelf.itc_lms_unofficial.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun <T> RecyclerView.set(
    items: List<T>,
    adapterClass: Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
    vararg relatedView: View
) {
    if (items.isEmpty()) {
        visibility = GONE
        relatedView.forEach {
            it.visibility = GONE
        }
        return
    }

    visibility = VISIBLE
    relatedView.forEach {
        it.visibility = VISIBLE
    }

    if (layoutManager == null) {
        layoutManager = LinearLayoutManager(context)
    }
    if (adapter == null) {
        adapter = adapterClass.constructors.first().newInstance(items) as RecyclerView.Adapter<*>?
    } else if (adapter is UpdatableAdapter<*, *>) {
        @Suppress("UNCHECKED_CAST")
        (adapter as UpdatableAdapter<T, *>).items = items
    }
    isNestedScrollingEnabled = false
}
