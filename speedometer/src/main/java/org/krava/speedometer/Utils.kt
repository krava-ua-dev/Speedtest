package org.krava.speedometer

import android.content.res.Resources

/**
 * Created by evheniikravchyna on 02.12.2017.
 */

object Utils {

    fun dp2px(resources: Resources, dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(resources: Resources, sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }
}
