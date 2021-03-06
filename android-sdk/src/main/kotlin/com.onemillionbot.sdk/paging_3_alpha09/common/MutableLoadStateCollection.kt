/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onemillionbot.sdk.paging_3_alpha09.common

import androidx.annotation.RestrictTo
import com.onemillionbot.sdk.paging_3_alpha09.common.CombinedLoadStates
import com.onemillionbot.sdk.paging_3_alpha09.common.LoadState
import com.onemillionbot.sdk.paging_3_alpha09.common.LoadStates
import com.onemillionbot.sdk.paging_3_alpha09.common.LoadType

/**
 * TODO: Remove this once [PageEvent.LoadStateUpdate] contained [CombinedLoadStates].
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class MutableLoadStateCollection {
    private var source: LoadStates = LoadStates.IDLE
    private var mediator: LoadStates? = null

    fun snapshot() = CombinedLoadStates(source, mediator)

    fun set(combinedLoadStates: CombinedLoadStates) {
        source = combinedLoadStates.source
        mediator = combinedLoadStates.mediator
    }

    fun set(type: LoadType, remote: Boolean, state: LoadState): Boolean {
        return if (remote) {
            val lastMediator = mediator
            mediator = (mediator ?: LoadStates.IDLE).modifyState(type, state)
            mediator != lastMediator
        } else {
            val lastSource = source
            source = source.modifyState(type, state)
            source != lastSource
        }
    }

    fun get(type: LoadType, remote: Boolean): LoadState? {
        return (if (remote) mediator else source)?.get(type)
    }

    internal inline fun forEach(op: (LoadType, Boolean, LoadState) -> Unit) {
        source.forEach { type, state ->
            op(type, false, state)
        }
        mediator?.forEach { type, state ->
            op(type, true, state)
        }
    }
}
