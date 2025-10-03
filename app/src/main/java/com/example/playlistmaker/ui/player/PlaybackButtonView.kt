package com.example.playlistmaker.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface OnToggleListener {
        fun onPlay()
        fun onPause()
    }

    private var iconPlayResId: Int = 0
    private var iconPauseResId: Int = 0

    private val drawBoundsF: RectF = RectF()
    private val drawBounds: Rect = Rect()

    private var playDrawable: Drawable? = null
    private var pauseDrawable: Drawable? = null

    var isPlaying: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var onToggleListener: OnToggleListener? = null

    init {
        isClickable = true
        isFocusable = true

        val a = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            0
        )
        try {
            iconPlayResId = a.getResourceId(
                R.styleable.PlaybackButtonView_iconPlay,
                R.drawable.ic_play_button
            )
            iconPauseResId = a.getResourceId(
                R.styleable.PlaybackButtonView_iconPause,
                R.drawable.ic_pause_button
            )
        } finally {
            a.recycle()
        }

        playDrawable = AppCompatResources.getDrawable(context, iconPlayResId)
        pauseDrawable = AppCompatResources.getDrawable(context, iconPauseResId)

    }

    fun toggle() {
        isPlaying = !isPlaying
        if (isPlaying) {
            onToggleListener?.onPlay()
        } else {
            onToggleListener?.onPause()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = suggestedMinimumWidth.coerceAtLeast(suggestedMinimumHeight)
        val w = resolveSize(desiredSize, widthMeasureSpec)
        val h = resolveSize(desiredSize, heightMeasureSpec)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        val right = (w - paddingRight).toFloat()
        val bottom = (h - paddingBottom).toFloat()
        drawBoundsF.set(left, top, right, bottom)
        drawBounds.set(
            drawBoundsF.left.toInt(),
            drawBoundsF.top.toInt(),
            drawBoundsF.right.toInt(),
            drawBoundsF.bottom.toInt()
        )
        playDrawable?.bounds = drawBounds
        pauseDrawable?.bounds = drawBounds
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = if (isPlaying) pauseDrawable else playDrawable
        d?.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                return true
            }
            MotionEvent.ACTION_UP -> {
                isPressed = false
                if (isPointInside(event.x, event.y)) {
                    toggle()
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun isPointInside(x: Float, y: Float): Boolean {
        return x >= 0 && x <= width && y >= 0 && y <= height
    }

}
