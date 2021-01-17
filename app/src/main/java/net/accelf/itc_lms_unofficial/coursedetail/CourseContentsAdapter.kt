package net.accelf.itc_lms_unofficial.coursedetail

import net.accelf.itc_lms_unofficial.databinding.ItemCourseContentBinding
import net.accelf.itc_lms_unofficial.models.CourseContent
import net.accelf.itc_lms_unofficial.util.UpdatableAdapter
import net.accelf.itc_lms_unofficial.util.fromHtml
import net.accelf.itc_lms_unofficial.util.setWithoutInitAdapter
import net.accelf.itc_lms_unofficial.util.timeSpanToString

class CourseContentsAdapter(
    items: List<CourseContent>,
    private val listener: MaterialListener,
    private val viewModel: CourseDetailViewModel,
) : UpdatableAdapter<CourseContent, ItemCourseContentBinding>(items,
    ItemCourseContentBinding::class.java) {

    override fun onBindViewHolder(holder: ViewHolder<ItemCourseContentBinding>, position: Int) {
        val item = items[position]

        holder.binding.apply {
            titleCourseContent.text = item.title
            textCourseContentDate.apply {
                text = context.timeSpanToString(item.from, item.until)
            }
            textCourseContentSummary.text = item.summary.fromHtml()

            listMaterials.adapter = null
            listMaterials.setWithoutInitAdapter(item.materials, headerMaterials) {
                MaterialsAdapter(item.materials, listener, viewModel)
            }

            viewModel.focusCourseContentResourceId?.let { id ->
                item.materials.firstOrNull { it.materialId == id }?.let {
                    expandableMaterials.isExpanded = true
                }
            }
        }
    }
}
