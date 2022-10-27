package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.utils.dp
import com.udacity.utils.sp
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f
    private var buttonTitle = resources.getString(R.string.button_name)
    private lateinit var rect: Rect

    private var progressValue = 0f
    private var valueAnimator = ValueAnimator()

    private var buttonColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var loadingColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circleColor = ContextCompat.getColor(context, R.color.colorAccent)


    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 3.dp
        textSize = 16.sp
        textAlign = Paint.Align.CENTER
    }


    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                isEnabled = false
                invalidate()
            }
            ButtonState.Loading -> {
                buttonTitle = resources.getString(R.string.button_loading)
                initValueAnimator()
                invalidate()
            }
            ButtonState.Completed -> {
//                completeBtn()
            }
        }
    }

    private fun completeBtn() {
        isEnabled = true
        valueAnimator.cancel()
        progressValue = 0f
        buttonTitle = resources.getString(R.string.button_name)
        invalidate()
    }


    init {
        context.withStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            defStyleAttr,
            0
        ) {

            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, buttonColor)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, loadingColor)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, circleColor)
        }
    }

    private fun initValueAnimator() {
        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat()).setDuration(4000).apply {
            addUpdateListener {
                progressValue = it.animatedValue as Float
                invalidate()
            }

            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    progressValue = 0f
                    if (buttonState == ButtonState.Loading) {
                        buttonState = ButtonState.Loading
                    }
                    else if(buttonState == ButtonState.Completed){
                        completeBtn()
                    }
                }
            })
        }
        valueAnimator.start()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawLoading(canvas)
        drawText(canvas)
        drawCircle(canvas)

    }

    private fun drawButton(canvas: Canvas) {
        paint.color = buttonColor
        canvas.drawRect(rect, paint)
    }

    private fun drawText(canvas: Canvas) {

       /* var buttonTitle = when (buttonState) {
            ButtonState.Loading -> resources.getString(R.string.button_loading)
            else -> resources.getString(R.string.button_name)
        }*/

        textWidth = paint.measureText(buttonTitle)
        paint.color = Color.WHITE
        canvas.drawText(
            buttonTitle,
            widthSize / 2.0f ,
            (heightSize / 2.0f) + 16,
            paint
        )

    }

    private fun drawLoading(canvas: Canvas) {
        paint.color = loadingColor
        canvas.drawRect(0f, 0f, progressValue, heightSize.toFloat(), paint)
    }

    private fun drawCircle(canvas: Canvas) {
        paint.color = circleColor
        canvas.drawArc(
            RectF(
                widthSize-200f,20f,
                /*widthSize / 2 + textWidth,
                heightSize / 2 - textSizeF / 2,*/
                widthSize-100f,
                heightSize-20f
            ), 0f, ((widthSize.toFloat() / 360) * progressValue) * 0.360f, true, paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

        rect = Rect(0, 0, widthSize, heightSize)

    }

}