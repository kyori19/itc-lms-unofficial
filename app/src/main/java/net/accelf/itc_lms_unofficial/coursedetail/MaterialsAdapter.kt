package net.accelf.itc_lms_unofficial.coursedetail

import android.os.Handler
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_material.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import net.accelf.itc_lms_unofficial.R
import net.accelf.itc_lms_unofficial.models.Material
import net.accelf.itc_lms_unofficial.models.Material.MaterialType
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class MaterialsAdapter(
    items: List<Material>,
    private val listener: MaterialListener,
    private val viewModel: CourseDetailViewModel,
) : UpdatableAdapter<Material, MaterialsAdapter.ViewHolder>(items), CoroutineScope by MainScope() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.apply {
            root.apply {
                setOnClickListener {
                    when (item.type) {
                        MaterialType.FILE -> {
                            listener.openFile(item)
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

                viewModel.focusCourseContentResourceId?.let {
                    if (it != item.materialId) {
                        return@let
                    }

                    dispatchTouchEvent(getTouchEvent(MotionEvent.ACTION_DOWN))
                    Handler(context.mainLooper).post {
                        Thread.sleep(1000)
                        dispatchTouchEvent(getTouchEvent(MotionEvent.ACTION_CANCEL))
                        viewModel.onCourseContentOpened()
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

    companion object {

        fun View.getTouchEvent(type: Int): MotionEvent {
            val now = SystemClock.uptimeMillis()
            return MotionEvent.obtain(now, now, type, width.toFloat() / 2, height.toFloat() / 2, 0)
        }
    }
}
