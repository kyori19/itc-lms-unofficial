package net.accelf.itc_lms_unofficial.coursedetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.item_material.view.*
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.models.Material.MaterialType
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class MaterialsAdapter(
    private val items: List<Material>
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

                    }
                    MaterialType.LINK -> {
                        item.url?.let { url ->
                            MaterialAlertDialogBuilder(it.context)
                                .setTitle(R.string.dialog_title_open_link)
                                .setMessage(
                                    it.context.getString(
                                        R.string.dialog_message_open_link,
                                        url
                                    )
                                )
                                .setPositiveButton(R.string.button_dialog_open) { _, _ ->
                                    val intent = CustomTabsIntent.Builder()
                                        .setShowTitle(true)
                                        .setToolbarColor(
                                            ContextCompat.getColor(
                                                it.context,
                                                R.color.colorPrimary
                                            )
                                        )
                                        .build()

                                    intent.launchUrl(it.context, url.toUri())
                                }
                                .setNeutralButton(R.string.button_dialog_close) { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }
                    MaterialType.VIDEO -> {
                        MaterialAlertDialogBuilder(it.context)
                            .setTitle(R.string.dialog_title_open_video)
                            .setMessage(R.string.dialog_message_open_video_unsupported)
                            .setPositiveButton(R.string.button_dialog_close) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
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

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root = view
        val iconMaterialType: ImageView = view.iconMaterialType
        val textMaterialName: TextView = view.textMaterialName
        val textMaterialDate: TextView = view.textMaterialDate
    }
}
