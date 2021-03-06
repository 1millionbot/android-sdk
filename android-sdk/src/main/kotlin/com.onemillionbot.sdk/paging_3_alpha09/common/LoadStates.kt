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

/**
 * Collection of pagination [LoadState]s - refresh, prepend, and append.
 */
data class LoadStates(
    /** [LoadState] corresponding to [LoadType.REFRESH] loads. */
    val refresh: LoadState,
    /** [LoadState] corresponding to [LoadType.PREPEND] loads. */
    val prepend: LoadState,
    /** [LoadState] corresponding to [LoadType.APPEND] loads. */
    val append: LoadState
) {
    init {
        require(!refresh.endOfPaginationReached) {
            "Refresh state may not set endOfPaginationReached = true"
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    inline fun forEach(op: (LoadType, LoadState) -> Unit) {
        op(LoadType.REFRESH, refresh)
        op(LoadType.PREPEND, prepend)
        op(LoadType.APPEND, append)
    }

    internal fun modifyState(loadType: LoadType, newState: LoadState): LoadStates {
        return when (loadType) {
            LoadType.APPEND -> copy(
                append = newState
            )
            LoadType.PREPEND -> copy(
                prepend = newState
            )
            LoadType.REFRESH -> copy(
                refresh = newState
            )
        }
    }

    internal fun get(loadType: LoadType) = when (loadType) {
        LoadType.REFRESH -> refresh
        LoadType.APPEND -> append
        LoadType.PREPEND -> prepend
    }

    internal companion object {
        val IDLE = LoadStates(
            refresh = LoadState.NotLoading.Incomplete,
            prepend = LoadState.NotLoading.Incomplete,
            append = LoadState.NotLoading.Incomplete
        )
    }
}
