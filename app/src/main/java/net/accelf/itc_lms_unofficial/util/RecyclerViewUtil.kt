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

    layoutManager = LinearLayoutManager(context)
    adapter = adapterClass.constructors.first().newInstance(items) as RecyclerView.Adapter<*>?
    isNestedScrollingEnabled = false
}
