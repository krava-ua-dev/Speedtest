package ua.dev.krava.speedtest.presentation.features.speedtest

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator

/**
 * Created by evheniikravchyna on 10.01.2018.
 */
class FlickerAnimation(view: View) {
    private val colorAnimation: ValueAnimator = ValueAnimator.ofFloat(1.0f, 0.2f, 1.0f).setDuration(800)

    init {
        colorAnimation.repeatCount = Animation.INFINITE
        colorAnimation.interpolator = LinearInterpolator()
        colorAnimation.addUpdateListener {
            view.alpha = it.animatedValue as Float
        }
    }


    fun start() {
        if (!colorAnimation.isRunning) colorAnimation.start()
    }

    fun cancel() {
        if (colorAnimation.isRunning) colorAnimation.end()
    }
}