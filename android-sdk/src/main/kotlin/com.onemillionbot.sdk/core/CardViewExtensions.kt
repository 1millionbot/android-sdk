package com.onemillionbot.sdk.core

import android.view.ViewGroup
import androidx.core.view.updateMargins
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.onemillionbot.sdk.R
import com.onemillionbot.sdk.entities.Message

fun MaterialCardView.setCorners(
    isPrevMessageFromOtherActor: Boolean,
    message: Message,
    onlyApplyMarginTop: Boolean = false
) {
    if (isPrevMessageFromOtherActor) {
        val radius = context.resources.getDimension(R.dimen.corner_size_message)

        if (!onlyApplyMarginTop) {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .apply {
                    if (message is Message.Bot) {
                        setTopLeftCornerSize(0f)
                    } else {
                        setTopRightCornerSize(0f)
                    }
                }
                .build()
        } else {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, radius)
                .build()
        }


        val margin = context.resources.getDimension(R.dimen.margin).toInt()
        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            updateMargins(top = margin)
        }
    } else {
        val radius = context.resources.getDimension(R.dimen.corner_size_message)

        shapeAppearanceModel = shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).apply {
            updateMargins(top = 0)
        }
    }
}
