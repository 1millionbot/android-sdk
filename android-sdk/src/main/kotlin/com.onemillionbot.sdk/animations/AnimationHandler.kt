package com.onemillionbot.sdk.animations

import android.animation.Animator
import android.view.View
import java.util.ArrayList

class AnimationHandler(
    private val animator: BaseViewAnimator) {
    private val callbacks: MutableList<Animator.AnimatorListener> = ArrayList()
    private var duration = 1000L

    fun duration(duration: Long): AnimationHandler {
        this.duration = duration
        return this
    }

    fun onEnd(callback: (animator: Animator?) -> Unit): AnimationHandler {
        callbacks.add(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) = callback(animation)
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return this
    }

    fun playOn(target: View) {
        animator.apply {
            setTarget(target)
            for (callback in callbacks) animator.addAnimatorListener(callback)
            setDuration(duration).animate()
        }
    }
}

