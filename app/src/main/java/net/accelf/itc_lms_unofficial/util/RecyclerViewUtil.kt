package net.accelf.itc_lms_unofficial.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun <T> RecyclerView.set(
    items: List<T>,
    adapterClass: Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
    vararg relatedView: View,
) {
    set(items, adapterClass, false, *relatedView)
}

fun <T> RecyclerView.set(
    items: List<T>,
    adapterClass: Class<RecyclerView.Adapter<RecyclerView.ViewHolder>>,
    divider: Boolean = false,
    vararg relatedView: View,
) {
    setWithoutInitAdapter(items, *relatedView) {
        adapterClass.constructors.first().newInstance(items) as RecyclerView.Adapter<*>
    }
    if (divider) {
        addItemDecoration(BetweenDividerDecoration(context))
    }
}

fun <T> RecyclerView.setWithoutInitAdapter(
    items: List<T>,
    vararg relatedView: View,
    initAdapter: () -> RecyclerView.Adapter<out RecyclerView.ViewHolder>
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
        adapter = initAdapter()
    } else if (adapter is UpdatableAdapter<*, *>) {
        @Suppress("UNCHECKED_CAST")
        (adapter as UpdatableAdapter<T, *>).items = items
    }
    isNestedScrollingEnabled = false
}
