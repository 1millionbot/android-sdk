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

/**
 * Load access information blob, containing information from presenter.
 */
internal data class ViewportHint(
    /** Page index offset from initial load */
    val pageOffset: Int,
    /**
     * Original index of item in the [Page] with [pageOffset].
     *
     * Three cases to consider:
     *  - [indexInPage] in Page.data.indices -> Hint references original item directly
     *  - [indexInPage] > Page.data.indices -> Hint references a placeholder after the last
     *    presented item.
     *  - [indexInPage] < 0 -> Hint references a placeholder before the first presented item.
     */
    val indexInPage: Int,
    /**
     * Distance from hint to first loaded item: `anchorPosition - firstLoadedItemPosition`
     *
     * Zero indicates access at boundary
     * Positive -> Within loaded range or in placeholders if greater than size of last page.
     * Negative -> placeholder access.
     *
     * Note: Does not include placeholders.
     */
    val presentedItemsBefore: Int,
    /**
     * Distance from hint to last presented item: `size - index - placeholdersAfter - 1`
     *
     * Zero indicates access at boundary
     * Positive -> Within loaded range or in placeholders if greater than size of last page.
     * Negative -> placeholder access.
     *
     * Note: Does not include placeholders.
     */
    val presentedItemsAfter: Int,
    /**
     * [hintOriginalPageOffset][TransformablePage.hintOriginalPageOffset] of the first presented
     * [TransformablePage] when this [ViewportHint] was created.
     */
    val originalPageOffsetFirst: Int,
    /**
     * [hintOriginalPageOffset][TransformablePage.hintOriginalPageOffset] of the last presented
     * [TransformablePage] when this [ViewportHint] was created.
     */
    val originalPageOffsetLast: Int
)
