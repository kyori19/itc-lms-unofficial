package net.accelf.itc_lms_unofficial.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from
import com.uber.autodispose.autoDispose
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T : Any?> Single<T>.call(owner: LifecycleOwner): SingleSubscribeProxy<T> {
    return observeOn(AndroidSchedulers.mainThread())
        .autoDispose(from(owner, Lifecycle.Event.ON_DESTROY))
}
