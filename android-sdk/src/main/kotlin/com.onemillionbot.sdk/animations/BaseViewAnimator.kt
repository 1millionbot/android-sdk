package com.onemillionbot.sdk.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import androidx.core.view.ViewCompat

abstract class BaseViewAnimator(
    protected val animatorAgent: AnimatorSet = AnimatorSet(),
    private var duration: Long = 1000
) {
    protected abstract fun prepare(target: View)

    fun setTarget(target: View): BaseViewAnimator {
        reset(target)
        prepare(target)
        return this
    }

    fun animate() {
        animatorAgent.duration = duration
        animatorAgent.start()
    }

    fun setDuration(duration: Long): BaseViewAnimator {
        this.duration = duration
        return this
    }

    fun addAnimatorListener(l: Animator.AnimatorListener?): BaseViewAnimator {
        animatorAgent.addListener(l)
        return this
    }

    private fun reset(target: View?) {
        ViewCompat.setAlpha(target, 1f)
        ViewCompat.setScaleX(target, 1f)
        ViewCompat.setScaleY(target, 1f)
        ViewCompat.setTranslationX(target, 0f)
        ViewCompat.setTranslationY(target, 0f)
        ViewCompat.setRotation(target, 0f)
        ViewCompat.setRotationY(target, 0f)
        ViewCompat.setRotationX(target, 0f)
    }
}

val bounceIn
    get() = object : BaseViewAnimator() {
        public override fun prepare(target: View) {
            animatorAgent.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0f, 1f, 1f, 1f),
                ObjectAnimator.ofFloat(target, "scaleX", 0.3f, 1.05f, 0.9f, 1f),
                ObjectAnimator.ofFloat(target, "scaleY", 0.3f, 1.05f, 0.9f, 1f)
            )
        }
    }

val fadeInDown
    get() = object : BaseViewAnimator() {
        public override fun prepare(target: View) {
            animatorAgent.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(target, "translationY", (-target.height / 4).toFloat(), 0f)
            )
        }
    }

val fadeInRight
    get() = object : BaseViewAnimator() {
        public override fun prepare(target: View) {
            animatorAgent.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(target, "translationX", (target.width / 4).toFloat(), 0f)
            )
        }
    }

val fadeOutRight
    get() = object : BaseViewAnimator() {
        public override fun prepare(target: View) {
            animatorAgent.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1f, 0f),
                ObjectAnimator.ofFloat(target, "translationX", 0f, (target.width / 4).toFloat())
            )
        }
    }
