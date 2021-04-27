package com.onemillionbot.sdk.core

import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.visibility(isVisible: Boolean) {
    val layoutParams: ViewGroup.LayoutParams = layoutParams
    if (layoutParams is CoordinatorLayout.LayoutParams) {
        val behavior = layoutParams.behavior
        if (behavior is HideBottomViewOnScrollBehavior) {
            if (isVisible) {
                behavior.slideUp(this)
            } else {
                behavior.slideDown(this)
            }
        }
    }
}
