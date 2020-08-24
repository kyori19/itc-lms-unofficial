package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_material.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.models.Material.MaterialType
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class MaterialsAdapter(
    private val items: List<Material>,
    private val listener: MaterialListener
) : RecyclerView.Adapter<MaterialsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            root.setOnClickListener {
                when (item.type) {
                    MaterialType.FILE -> {
                        item.apply {
                            listener.openFile(
                                materialId,
                                resourceId,
                                file!!.fileName,
                                file.objectName,
                                until!!
                            )
                        }
                    }
                    MaterialType.LINK -> {
                        item.url?.let { url ->
                            listener.openLink(url)
                        }
                    }
                    MaterialType.VIDEO -> {
                        listener.openVideo()
                    }
                }
            }

            iconMaterialType.apply {
                setImageResource(
                    when (item.type) {
                        MaterialType.FILE -> R.drawable.ic_file
                        MaterialType.LINK -> R.drawable.ic_link
                        MaterialType.VIDEO -> R.drawable.ic_video
                    }
                )
                contentDescription = context.getString(
                    when (item.type) {
                        MaterialType.FILE -> R.string.hint_icon_file
                        MaterialType.LINK -> R.string.hint_icon_link
                        MaterialType.VIDEO -> R.string.hint_icon_video
                    }
                )
            }

            textMaterialName.text = item.name

            textMaterialDate.apply {
                text = context.timeSpanToString(item.createdAt, item.until)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val iconMaterialType: ImageView = view.iconMaterialType
        val textMaterialName: TextView = view.textMaterialName
        val textMaterialDate: TextView = view.textMaterialDate
    }
}
