package ru.sad.base.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.google.android.play.integrity.internal.t
import ru.sad.base.R
import ru.sad.base.ext.tint
import ru.sad.base.ext.toPx

open class BottomBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val items = ArrayList<ImageView>()

    private lateinit var layoutItems: LinearLayout

    private var lastSelected = 0

    init {
        isDrawingCacheEnabled = true
        init()
    }

    private fun init() {
        inflate(context, R.layout.view_bottom_bar, this)
        layoutItems = findViewById(R.id.layoutBottomBar)
    }

    fun hide() {
        isGone = true
    }

    fun show() {
        isVisible = true
    }

    fun addItem(@DrawableRes drawableRes: Int, onClick: () -> Unit) {
        val image = createItem(drawableRes, onClick)
        layoutItems.addView(image)
        items.add(image)
    }

    fun updateColor(index: Int) {
        try {
            items[index].tint(R.color.main_second_color)
            updateIconsTint(items[index].id)
        } catch (e: Exception) {

        }
    }

    private fun createItem(@DrawableRes drawableRes: Int, onClick: () -> Unit): ImageView {
        val image = ImageView(context)

        with(image) {
            id = items.size + 1

            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, 40.toPx.toInt()).apply {
                weight = 1f
                setPadding(10)
            }

            adjustViewBounds = true

            if (id == 2) {
                lastSelected = 2
                tint(R.color.main_second_color)
            } else {
                tint(R.color.gray)
            }

            setImageDrawable(ContextCompat.getDrawable(context, drawableRes))

            setOnClickListener {
                onClick.invoke()
                tint(R.color.main_second_color)
                updateIconsTint(this.id)
            }

            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        return image
    }

    private fun updateIconsTint(selected: Int) {
        if (selected != lastSelected) {
            layoutItems.findViewById<ImageView>(lastSelected)?.tint(R.color.gray)
            lastSelected = selected
        }
    }
}