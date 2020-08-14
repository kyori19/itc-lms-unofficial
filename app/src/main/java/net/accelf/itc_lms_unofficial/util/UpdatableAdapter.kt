package net.accelf.itc_lms_unofficial.util

import androidx.recyclerview.widget.RecyclerView

abstract class UpdatableAdapter<T, VH : RecyclerView.ViewHolder?>(items: List<T>) :
    RecyclerView.Adapter<VH>() {

    var items = items
        set(value) {
            field = value

            notifyDataSetChanged()
        }
}
