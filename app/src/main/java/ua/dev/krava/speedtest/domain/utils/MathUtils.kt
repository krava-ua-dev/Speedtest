package ua.dev.krava.speedtest.domain.utils

import java.math.BigDecimal

/**
 * Created by evheniikravchyna on 07.01.2018.
 */
object MathUtils {

    fun round(d: Float, decimalPlace: Int): Float {
        var bd = BigDecimal(java.lang.Float.toString(d))
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
        return bd.toFloat()
    }

}