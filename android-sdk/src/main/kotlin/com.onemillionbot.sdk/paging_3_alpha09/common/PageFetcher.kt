/*
 * Copyright 2019 The Android Open Source Project
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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
internal class PageFetcher<Key : Any, Value : Any>(
    private val pagingSourceFactory: () -> PagingSource<Key, Value>,
    private val initialKey: Key?,
    private val config: PagingConfig,
    @OptIn(ExperimentalPagingApi::class)
    private val remoteMediator: RemoteMediator<Key, Value>? = null
) {
    /**
     * Channel of refresh signals that would trigger a new instance of [PageFetcherSnapshot].
     * Signals sent to this channel should be `true` if a remote REFRESH load should be triggered,
     * `false` otherwise.
     *
     * NOTE: This channel is conflated, which means it has a buffer size of 1, and will always
     *  broadcast the latest value received.
     */
    private val refreshChannel = ConflatedBroadcastChannel<Boolean>()

    private val retryChannel = ConflatedBroadcastChannel<Unit>()

    // The object built by paging builder can maintain the scope so that on rotation we don't stop
    // the paging.
    val flow: Flow<PagingData<Value>> = channelFlow {
        val remoteMediatorAccessor = remoteMediator?.let {
            RemoteMediatorAccessor(this, it)
        }
        refreshChannel.asFlow()
            .onStart {
                @OptIn(ExperimentalPagingApi::class)
                emit(remoteMediatorAccessor?.initialize() == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
            }
            .scan(null) { previousGeneration: PageFetcherSnapshot<Key, Value>?, triggerRemoteRefresh ->
                var pagingSource = generateNewPagingSource(previousGeneration?.pagingSource)
                while (pagingSource.invalid) {
                    pagingSource = generateNewPagingSource(previousGeneration?.pagingSource)
                }

                @OptIn(ExperimentalPagingApi::class)
                val initialKey: Key? = previousGeneration?.refreshKeyInfo()
                    ?.let { pagingSource.getRefreshKey(it) }
                    ?: initialKey

                previousGeneration?.close()

                PageFetcherSnapshot<Key, Value>(
                    initialKey = initialKey,
                    pagingSource = pagingSource,
                    config = config,
                    retryFlow = retryChannel.asFlow(),
                    // Only trigger remote refresh on refresh signals that do not originate from
                    // initialization or PagingSource invalidation.
                    triggerRemoteRefresh = triggerRemoteRefresh,
                    remoteMediatorConnection = remoteMediatorAccessor,
                    invalidate = this@PageFetcher::refresh
                )
            }
            .filterNotNull()
            .mapLatest { generation ->
                val downstreamFlow = if (remoteMediatorAccessor == null) {
                    generation.pageEventFlow
                } else {
                    generation.injectRemoteEvents(remoteMediatorAccessor)
                }
                PagingData(
                    flow = downstreamFlow,
                    receiver = PagerUiReceiver(generation, retryChannel)
                )
            }
            .collect { send(it) }
    }

    private fun PageFetcherSnapshot<Key, Value>.injectRemoteEvents(
        accessor: RemoteMediatorAccessor<Key, Value>
    ): Flow<PageEvent<Value>> = channelFlow {
        suspend fun dispatchIfValid(type: LoadType, state: LoadState) {
            // not loading events are sent w/ insert-drop events.
            if (PageEvent.LoadStateUpdate.canDispatchWithoutInsert(state, fromMediator = true)) {
                send(
                    PageEvent.LoadStateUpdate<Value>(type, true, state)
                )
            } else {
                // ignore. Some invalidation will happened and we'll send the event there instead
            }
        }
        launch {
            var prev = LoadStates.IDLE
            accessor.state.collect {
                if (prev.refresh != it.refresh) {
                    dispatchIfValid(LoadType.REFRESH, it.refresh)
                }
                if (prev.prepend != it.prepend) {
                    dispatchIfValid(LoadType.PREPEND, it.prepend)
                }
                if (prev.append != it.append) {
                    dispatchIfValid(LoadType.APPEND, it.append)
                }
                prev = it
            }
        }

        this@injectRemoteEvents.pageEventFlow.collect {
            // only insert events have combinedLoadStates.
            if (it is PageEvent.Insert<Value>) {
                send(
                    it.copy(
                        combinedLoadStates = CombinedLoadStates(
                            it.combinedLoadStates.source,
                            accessor.state.value
                        )
                    )
                )
            } else {
                send(it)
            }
        }
    }

    fun refresh() {
        refreshChannel.offer(true)
    }

    private fun invalidate() {
        refreshChannel.offer(false)
    }

    private fun generateNewPagingSource(
        previousPagingSource: PagingSource<Key, Value>?
    ): PagingSource<Key, Value> {
        val pagingSource = pagingSourceFactory()

        // Ensure pagingSourceFactory produces a new instance of PagingSource.
        check(pagingSource !== previousPagingSource) {
            """
            An instance of PagingSource was re-used when Pager expected to create a new
            instance. Ensure that the pagingSourceFactory passed to Pager always returns a
            new instance of PagingSource.
            """.trimIndent()
        }

        // Hook up refresh signals from PagingSource.
        pagingSource.registerInvalidatedCallback(::invalidate)
        previousPagingSource?.unregisterInvalidatedCallback(::invalidate)
        previousPagingSource?.invalidate() // Note: Invalidate is idempotent.

        return pagingSource
    }

    inner class PagerUiReceiver<Key : Any, Value : Any> constructor(
        private val pageFetcherSnapshot: PageFetcherSnapshot<Key, Value>,
        private val retryChannel: SendChannel<Unit>
    ) : UiReceiver {
        override fun accessHint(viewportHint: ViewportHint) {
            pageFetcherSnapshot.accessHint(viewportHint)
        }

        override fun retry() {
            retryChannel.offer(Unit)
        }

        override fun refresh() = this@PageFetcher.refresh()
    }
}
