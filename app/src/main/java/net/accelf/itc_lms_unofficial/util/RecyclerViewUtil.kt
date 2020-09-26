package net.accelf.itc_lms_unofficial.util

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

inline fun <T, reified A> RecyclerView.set(
    items: List<T>,
    vararg relatedView: View,
) {
    set<T, A>(items, false, *relatedView)
}

inline fun <T, reified A> RecyclerView.set(
    items: List<T>,
    divider: Boolean = false,
    vararg relatedView: View,
) {
    setWithoutInitAdapter(items, *relatedView) {
        A::class.java.constructors.first().newInstance(items) as RecyclerView.Adapter<*>
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
