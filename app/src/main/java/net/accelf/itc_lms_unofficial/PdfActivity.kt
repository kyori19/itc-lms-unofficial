package net.accelf.itc_lms_unofficial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import net.accelf.itc_lms_unofficial.util.TIME_SECONDS_FORMAT
import net.accelf.itc_lms_unofficial.util.replaceFragment
import java.util.*

@AndroidEntryPoint
class PdfActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(EXTRA_FILE_ID)) {
            val fileId = intent.getStringExtra(EXTRA_FILE_ID)
            val materialId = intent.getStringExtra(EXTRA_MATERIAL_ID)
            val endDate = intent.getStringExtra(EXTRA_END_DATE)

            replaceFragment(PdfFragment.newInstance(fileId, materialId, endDate))
            return
        }

        if (intent.hasExtra(EXTRA_OBJECT_NAME)) {
            val objectName = intent.getStringExtra(EXTRA_OBJECT_NAME)

            replaceFragment(PdfFragment.newInstance(objectName))
        }
    }

    companion object {
        private const val EXTRA_FILE_ID = "file_id"
        private const val EXTRA_MATERIAL_ID = "material_id"
        private const val EXTRA_END_DATE = "end_date"
        private const val EXTRA_OBJECT_NAME = "object_name"

        fun intent(context: Context, fileId: String, materialId: String, endDate: Date): Intent {
            return Intent(context, PdfActivity::class.java).apply {
                putExtra(EXTRA_FILE_ID, fileId)
                putExtra(EXTRA_MATERIAL_ID, materialId)
                putExtra(EXTRA_END_DATE, TIME_SECONDS_FORMAT.format(endDate))
            }
        }

        fun intent(context: Context, objectName: String): Intent {
            return Intent(context, PdfActivity::class.java).apply {
                putExtra(EXTRA_OBJECT_NAME, objectName)
            }
        }
    }
}
