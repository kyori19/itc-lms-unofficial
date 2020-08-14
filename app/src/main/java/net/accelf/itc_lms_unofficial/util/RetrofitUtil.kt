package net.accelf.itc_lms_unofficial.util

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.SingleSubscribeProxy
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider.from
import com.uber.autodispose.autoDispose
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import net.accelf.itc_lms_unofficial.StartLoginFragment
import retrofit2.HttpException

fun <T : Any?> Single<T>.call(owner: LifecycleOwner): SingleSubscribeProxy<T> {
    return observeOn(AndroidSchedulers.mainThread())
        .autoDispose(from(owner, Lifecycle.Event.ON_DESTROY))
}

fun <T : Any> Single<T>.withResponse(activity: AppCompatActivity, onSuccess: (T) -> Unit) {
    call(activity).subscribe(
        onSuccess,
        {
            if (it is HttpException && it.code() == 302) {
                activity.replaceFragment(StartLoginFragment.newInstance())
            } else {
                activity.replaceErrorFragment(it)
            }
        }
    )
}
