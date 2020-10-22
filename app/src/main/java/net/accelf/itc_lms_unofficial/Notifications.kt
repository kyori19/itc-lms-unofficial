package net.accelf.itc_lms_unofficial

class Notifications {

    class Ids {
        companion object {
            const val SESSION_EXPIRED = 10000001
            const val WRONG_CREDENTIALS = 10000002
            const val PERMISSION_REQUIRED = 20000000
            const val DOWNLOAD_PROGRESS = 30000001
        }
    }

    class Channels {
        companion object {
            const val LMS_UPDATES = "lms_updates"
            const val ERRORS = "errors"
            const val DOWNLOADS = "downloads"
        }
    }
}
