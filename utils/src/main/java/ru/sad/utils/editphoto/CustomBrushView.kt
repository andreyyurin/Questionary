package ru.sad.utils.editphoto

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import com.google.android.material.slider.LabelFormatter.LABEL_GONE
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnChangeListener
import ru.sad.utils.toDp
import ru.sad.utils.toPx


open class CustomBrushView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Slider(context, attrs, defStyleAttr) {

    companion object {
        private val mainColor = Color.parseColor("#3BB1FF")

        private val statesThumb = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_pressed)
        )

        private val colorsThumb = intArrayOf(
            mainColor,
            mainColor,
            mainColor,
            mainColor
        )
    }

    init {
        init()
    }

    var onChangeValue: ((Int) -> Unit)? = null

    private fun init() {
        setupDrawables()
        setupListeners()
    }

    private fun setupDrawables() {
        this.valueFrom = 10f
        this.valueTo = 60f
        this.value = 35f
        this.stepSize = 1f
        this.labelBehavior = LABEL_GONE
        this.thumbElevation = 0f
        this.thumbTintList = ColorStateList(statesThumb, colorsThumb)
        this.isTickVisible = false
        this.haloRadius = 0
    }

    private fun setupListeners() {
        this.addOnSliderTouchListener(object : OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {

                //slider.trackWidth = slider.value.toPx.toInt()
            }

            override fun onStopTrackingTouch(slider: Slider) {

            }
        })
        this.addOnChangeListener(OnChangeListener { slider, value, fromUser ->
            slider.thumbRadius = (value.toDp / 2).toInt()
            onChangeValue?.invoke(value.toInt())
        })
    }

}