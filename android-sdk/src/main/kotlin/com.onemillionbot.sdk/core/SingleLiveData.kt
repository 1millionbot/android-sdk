package com.onemillionbot.sdk.core

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * SingleLiveData is a MutableLiveData that helps us to send events to the consumer only once.
 *
 * Examples
 * - Showing a Toast,
 * - Showing Snackbar,
 * - Navigational Events (launching activities, changing fragments),
 *
 * With normal LiveData the last pushed event on a view restart would be delivered again and again
 * For example
 *  1. we get an error,
 *  2. push it to the MutableLiveData,
 *  3. Observer on the View consumes it and shows a toast.
 *  4. We rotate the screen
 *  5. Observer resubscribes and reads the last value, that would lead to show a toast again.
 *
 *  This class intends to solve this problem by limiting the number of consumer to 1.
 *
 *  As a side effect this also limits the number of observers to one,
 *  so at the same time this live data won't deliver the consumed event to more then 1 subscribers.
 *
 */
class SingleLiveData<T> : MutableLiveData<T>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {

        if (hasActiveObservers()) {
            throw IllegalStateException("Multiple observers are registered to a SingleLiveData!")
        }

        super.observe(owner, Observer<T> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(it)
            }
        })
    }

    override fun setValue(value: T) {
        pending.set(true)
        super.setValue(value)
    }

}
