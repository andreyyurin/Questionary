package ru.sad.base.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import ru.sad.base.R

class FeedbackView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val stars by lazy {
        arrayOf<ImageView>(
            findViewById(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )
    }

    var maxStar = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.view_feedback, this, true)

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (measuredHeight > 0) {
                    viewTreeObserver.removeOnPreDrawListener(this)
                }
                return true
            }
        })

        stars.forEachIndexed { index, star ->
            star.setImageResource(R.drawable.ic_star_border)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun setText(text: String) {
        findViewById<TextView>(R.id.label).text = text
    }

    fun setOnStarListener(onClick: () -> Unit) {
        var isClicked = false
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                for (i in 0 until index + 1) {
                    stars[i].setImageResource(R.drawable.ic_star_filled)
                }
                for (i in index + 1 until stars.size) {
                    stars[i].setImageResource(R.drawable.ic_star_border)
                }
                maxStar = index + 1

                if (!isClicked) {
                    onClick.invoke()
                    isClicked = true
                }
            }
        }
    }

    companion object {
        private const val answersCount = 5
    }
}