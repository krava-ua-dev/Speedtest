package org.krava.speedometer

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import java.math.BigDecimal


/**
 * Created by evheniikravchyna on 02.12.2017.
 */

class SpeedometerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private lateinit var gradient: LinearGradient
    private lateinit var paintArcIn: Paint
    private lateinit var paintArcInDots: Paint
    private lateinit var paintArcOutLight: Paint
    private lateinit var paintArcOut: Paint
    private lateinit var indicatorPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var speedTextPaint: Paint
    private val METRIC = "MB"
    private val speedScales = arrayOf(0, 1, 5, 10, 20, 30, 40, 60, 80)
    private val speedRanges = arrayOf(0.0f..1.0f, 1.0f..5.0f, 5.0f..10.0f, 10.0f..20.0f, 20.0f..30.0f, 30.0f..40.0f, 40.0f..60.0f, 60.0f..80.0f)

    private val indicatorPath = Path()

    private val rectInF = RectF()
    private val rectInDotsF = RectF()
    private val rectOutF = RectF()
    private val rectSpeedF = RectF()


    private var strokeWidth: Float = 0.toFloat()
    private var bottomTextSize: Float = 0.toFloat()
    private var textSize: Float = 0.toFloat()
    private var textColor: Int = 0
    private var progressAnimator: ValueAnimator? = null

    fun setProgress(newProgress: Float) {
            if (round(newProgress, 2).toFloat() != progress && !(newProgress >= max && progress == max.toFloat())) {
                if (newProgress > max) {
                    initProgressAnimator(max.toFloat())
                } else {
                    initProgressAnimator(round(newProgress, 2).toFloat())
                }
            }
    }
    private var progress = 0f

    private var max: Int = 0
        set(max) {
            if (max > 0) {
                field = max
                invalidate()
            }
        }
    private var arcAngle: Float = 0.toFloat()
    private var suffixTextPadding: Float = 0.toFloat()

    private var arcBottomHeight: Float = 0.toFloat()

    private val default_text_color = Color.rgb(66, 145, 241)
    private val default_suffix_padding: Float
    private val default_bottom_text_size: Float
    private val default_stroke_width: Float
    private val default_max = 80
    private val default_arc_angle = 360 * 0.66f
    private var default_text_size: Float = 0.toFloat()
    private val min_size: Int

    private fun initProgressAnimator(new: Float, duration: Long = 245) {
        progressAnimator?.let { if (it.isRunning) it.cancel() }
        progressAnimator = ValueAnimator.ofFloat(progress, new)
        progressAnimator?.addUpdateListener {
            invalidate()
        }
        progressAnimator?.duration = duration
        progressAnimator?.start()
    }

    private val indicatorWidth: Float
        get() = Utils.dp2px(resources, 4f)

    private val bottomText: String
        get() = "Bits Per Second"

    init {
        default_text_size = Utils.sp2px(resources, 18f)
        min_size = Utils.dp2px(resources, 100f).toInt()
        default_text_size = Utils.sp2px(resources, 40f)
        default_suffix_padding = Utils.dp2px(resources, 4f)
        default_bottom_text_size = Utils.sp2px(resources, 11f)
        default_stroke_width = Utils.dp2px(resources, 4f)

        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.SpeedometerView, defStyleAttr, 0)
        initByAttributes(attributes)
        attributes.recycle()

        initPainters()
    }

    fun animateToZero() {
        if (progress != 0.0f) {
            initProgressAnimator(0.0f, 1400)
        }
    }

    private fun initPainters() {
        paintArcInDots = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paintArcInDots.strokeWidth = strokeWidth / 2.5f
        paintArcInDots.style = Paint.Style.STROKE
        paintArcInDots.strokeCap = Paint.Cap.ROUND

        textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.argb(30, 255, 255, 255)

        speedTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        speedTextPaint.textSize = Utils.sp2px(resources, 9f)

        indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        indicatorPaint.color = Color.WHITE

        paintArcIn = Paint(Paint.ANTI_ALIAS_FLAG)
        paintArcIn.strokeWidth = strokeWidth
        paintArcIn.style = Paint.Style.STROKE
        paintArcIn.strokeCap = Paint.Cap.ROUND

        paintArcOut = Paint(Paint.ANTI_ALIAS_FLAG)
        paintArcOut.strokeWidth = strokeWidth / 2.5f
        paintArcOut.style = Paint.Style.STROKE
        paintArcOut.strokeCap = Paint.Cap.ROUND

        paintArcOutLight = Paint(Paint.ANTI_ALIAS_FLAG)
        paintArcOutLight.strokeWidth = strokeWidth / 2.5f
        paintArcOutLight.style = Paint.Style.STROKE
        paintArcOutLight.strokeCap = Paint.Cap.ROUND
    }

    private fun initIndicator() {
        indicatorPath.reset()

        val centerX = rectInF.centerX()
        val startY = Utils.dp2px(resources, 56f)

        indicatorPath.moveTo(centerX, startY)
        val indicatorBottom = rectInF.centerY()
        indicatorPath.lineTo(centerX - indicatorWidth, indicatorBottom)
        indicatorPath.lineTo(centerX + indicatorWidth, indicatorBottom)
        val rectF = RectF(centerX - indicatorWidth, indicatorBottom - indicatorWidth, centerX + indicatorWidth, indicatorBottom + indicatorWidth)
        indicatorPath.addArc(rectF, 0f, 180f)
    }

    private fun initByAttributes(attributes: TypedArray) {
        textColor = attributes.getColor(R.styleable.SpeedometerView_spd_text_color, default_text_color)
        textSize = attributes.getDimension(R.styleable.SpeedometerView_spd_text_size, default_text_size)
        arcAngle = attributes.getFloat(R.styleable.SpeedometerView_spd_angle, default_arc_angle)
        max = attributes.getInt(R.styleable.SpeedometerView_spd_max, default_max)
        progress = attributes.getFloat(R.styleable.SpeedometerView_spd_progress, 0f)
        strokeWidth = attributes.getDimension(R.styleable.SpeedometerView_spd_stroke_width, default_stroke_width)
        suffixTextPadding = attributes.getDimension(R.styleable.SpeedometerView_spd_suffix_text_padding, default_suffix_padding)
        bottomTextSize = attributes.getDimension(R.styleable.SpeedometerView_spd_bottom_text_size, default_bottom_text_size)

        dp30 = Utils.dp2px(resources, 30f) + strokeWidth
        dp16 = Utils.dp2px(resources, 16f)
    }

    private var dp30: Float = 0.0f
    private var dp16: Float = 0.0f

    override fun invalidate() {
        initPainters()
        super.invalidate()
    }

    override fun getSuggestedMinimumHeight(): Int = min_size

    override fun getSuggestedMinimumWidth(): Int = min_size

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec)

        rectInF.set(dp30, dp30, width - dp30, width - dp30)
        rectInDotsF.set(dp30 + strokeWidth, dp30 + strokeWidth, width.toFloat() - dp30 - strokeWidth, width.toFloat() - dp30 - strokeWidth)
        rectOutF.set(strokeWidth / 2.5f, strokeWidth / 2.5f, width - strokeWidth / 2.5f, width - strokeWidth / 2.5f)
        rectSpeedF.set(dp30 / 2f, dp30 / 2f, width - dp30 / 2f, width - dp30 / 2f)
        gradient = LinearGradient(0f, 0f, rectInF.right, 0f, Color.rgb(234, 27, 27), Color.rgb(108, 251, 1), Shader.TileMode.CLAMP)
        val radius = width / 2f
        val angle = (360 - arcAngle) / 2f
        arcBottomHeight = radius * (1 - Math.cos(angle / 180 * Math.PI)).toFloat()

        setMeasuredDimension(width, (width - arcBottomHeight + dp16).toInt())

        initIndicator()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        progressAnimator?.let {
            progress = it.animatedValue as Float
        }
        val startAngle = 270 - arcAngle / 2f

        paintArcOutLight.color = Color.argb(5, 255, 255, 255)
        canvas.drawArc(rectOutF, startAngle, arcAngle, false, paintArcOutLight)

        paintArcOut.color = Color.argb(13, 255, 255, 255)
        canvas.drawArc(rectOutF, startAngle, 57.9f, false, paintArcOut)
        canvas.drawArc(rectOutF, startAngle + 59.9f, 57.9f, false, paintArcOut)
        canvas.drawArc(rectOutF, startAngle + 119.8f, 57.9f, false, paintArcOut)
        canvas.drawArc(rectOutF, startAngle + 179.7f, 57.9f, false, paintArcOut)

        var i = 0f
        var alpha = startAngle
        while (i < speedScales.size) {
            val x = (rectSpeedF.centerX() + rectSpeedF.width() / 2f * Math.cos(alpha / 180 * Math.PI)).toFloat() - speedTextPaint.measureText("${speedScales[i.toInt()]}$METRIC") / 2f
            val y = (rectSpeedF.centerY() + rectSpeedF.width() / 2f * Math.sin(alpha / 180 * Math.PI)).toFloat() + speedTextPaint.descent()
            speedTextPaint.color = if (Math.round(progress) == speedScales[i.toInt()]) {
                Color.WHITE
            } else {
                Color.argb(60, 255, 255, 255)
            }
            speedTextPaint.typeface = if (Math.round(progress) == speedScales[i.toInt()]) {
                Typeface.DEFAULT_BOLD
            } else {
                Typeface.DEFAULT
            }
            canvas.drawText("${speedScales[i.toInt()]}$METRIC", x, y, speedTextPaint)
            i++
            alpha += arcAngle / (speedScales.size - 1)
        }
        //start == 144  arc = 237.6
        paintArcIn.shader = gradient
        canvas.drawArc(rectInF, startAngle, 58f, false, paintArcIn)
        canvas.drawArc(rectInF, startAngle + 60.8f, 87f, false, paintArcIn)
        canvas.drawArc(rectInF, startAngle + 150.6f, 87f, false, paintArcIn)

        paintArcInDots.color = Color.argb(40, 255, 255, 255)
        canvas.drawArc(rectInDotsF, startAngle, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 29, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 59, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 89.4f, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 118.8f, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 148.4f, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 178, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 208, 1f, false, paintArcInDots)
        canvas.drawArc(rectInDotsF, startAngle + 236.6f, 1f, false, paintArcInDots)

        textPaint.textSize = bottomTextSize
        val bottomTextBaseline = width.toFloat() - arcBottomHeight - textPaint.descent() * 2
        canvas.drawText(bottomText, (width - textPaint.measureText(bottomText)) / 2.0f, bottomTextBaseline, textPaint)
        canvas.save()

        val rangsPosition = findProgressPositionInRange()
        val kof  = round((29.7 * (rangsPosition + (progress - speedRanges[rangsPosition].start) / (speedRanges[rangsPosition].endInclusive - speedRanges[rangsPosition].start))).toFloat(), 2).toFloat()
        val degrees = startAngle + 90 + kof

        canvas.rotate(degrees, rectInF.centerX(), rectInF.centerY())
        canvas.drawPath(indicatorPath, indicatorPaint)
        canvas.restore()
    }

    private fun findProgressPositionInRange(): Int = when(progress) {
        in speedRanges[0] -> 0
        in speedRanges[1] -> 1
        in speedRanges[2] -> 2
        in speedRanges[3] -> 3
        in speedRanges[4] -> 4
        in speedRanges[5] -> 5
        in speedRanges[6] -> 6
        in speedRanges[7] -> 7
        else -> 0
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putFloat(INSTANCE_SUFFIX_TEXT_PADDING, suffixTextPadding)
        bundle.putFloat(INSTANCE_BOTTOM_TEXT_SIZE, bottomTextSize)
        bundle.putFloat(INSTANCE_TEXT_SIZE, textSize)
        bundle.putInt(INSTANCE_TEXT_COLOR, textColor)
        bundle.putFloat(INSTANCE_PROGRESS, progress)
        bundle.putInt(INSTANCE_MAX, max)
        bundle.putFloat(INSTANCE_ARC_ANGLE, arcAngle)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            suffixTextPadding = state.getFloat(INSTANCE_SUFFIX_TEXT_PADDING)
            bottomTextSize = state.getFloat(INSTANCE_BOTTOM_TEXT_SIZE)
            textSize = state.getFloat(INSTANCE_TEXT_SIZE)
            textColor = state.getInt(INSTANCE_TEXT_COLOR)
            max = state.getInt(INSTANCE_MAX)
            progress = state.getFloat(INSTANCE_PROGRESS)
            initPainters()
            super.onRestoreInstanceState(state.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    private fun round(d: Float, decimalPlace: Int): BigDecimal {
        var bd = BigDecimal(java.lang.Float.toString(d))
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
        return bd
    }

    override fun onDetachedFromWindow() {
        progressAnimator?.let { if (it.isRunning) it.cancel() }
        super.onDetachedFromWindow()
    }

    companion object {
        private val INSTANCE_STATE = "saved_instance"
        private val INSTANCE_SUFFIX_TEXT_PADDING = "suffix_text_padding"
        private val INSTANCE_BOTTOM_TEXT_SIZE = "bottom_text_size"
        private val INSTANCE_TEXT_SIZE = "text_size"
        private val INSTANCE_TEXT_COLOR = "text_color"
        private val INSTANCE_PROGRESS = "progress"
        private val INSTANCE_MAX = "max"
        private val INSTANCE_ARC_ANGLE = "arc_angle"
    }
}
