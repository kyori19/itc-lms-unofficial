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

        val fileId = intent.getStringExtra(EXTRA_FILE_ID)
        val materialId = intent.getStringExtra(EXTRA_MATERIAL_ID)
        val endDate = intent.getStringExtra(EXTRA_END_DATE)

        replaceFragment(PdfFragment.newInstance(fileId, materialId, endDate))
    }

    companion object {
        private const val EXTRA_FILE_ID = "file_id"
        private const val EXTRA_MATERIAL_ID = "material_id"
        private const val EXTRA_END_DATE = "end_date"

        fun intent(context: Context, fileId: String, materialId: String, endDate: Date): Intent {
            return Intent(context, PdfActivity::class.java).apply {
                putExtra(EXTRA_FILE_ID, fileId)
                putExtra(EXTRA_MATERIAL_ID, materialId)
                putExtra(EXTRA_END_DATE, TIME_SECONDS_FORMAT.format(endDate))
            }
        }
    }
}
