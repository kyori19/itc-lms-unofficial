package net.accelf.itc_lms_unofficial.network

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

@Suppress("FunctionName")
interface ProxyInterface {
    fun __special__dummy__()
}

class LMSProxy(
    private var lms: LMS,
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method?, args: Array<out Any>?): Any? {
        if (method?.name == "__special__dummy__") {
            lms = DummyLMS()
            return null
        }

        return method?.invoke(lms, *(args ?: arrayOf()))
    }
}
