package ru.sad.utils.fab

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import ru.sad.utils.R


open class AddFloatingActionButton : FloatingActionButton {
    var mPlusColor = 0

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
    }

//    override fun init(context: Context, attributeSet: AttributeSet?) {
//        val attr: TypedArray =
//            context.obtainStyledAttributes(attributeSet, R.styleable.AddFloatingActionButton, 0, 0)
//        mPlusColor = attr.getColor(
//            R.styleable.AddFloatingActionButton_fab_plusIconColor,
//            getColor(R.color.white)
//        )
//        attr.recycle()
//        super.init(context, attributeSet)
//    }

    /**
     * @return the current Color of plus icon.
     */
    var plusColor: Int
        get() = mPlusColor
        set(color) {
            if (mPlusColor != color) {
                mPlusColor = color
                updateBackground()
            }
        }

    fun setPlusColorResId(@ColorRes plusColor: Int) {
        mPlusColor = getColor(plusColor)
    }

    open val iconDraw: Drawable
        get() {
            val iconSize: Float = getDimension(R.dimen.fab_icon_size)
            val iconHalfSize = iconSize / 2f
            val plusSize: Float = getDimension(R.dimen.fab_plus_icon_size)
            val plusHalfStroke: Float = getDimension(R.dimen.fab_plus_icon_stroke) / 2f
            val plusOffset = (iconSize - plusSize) / 2f
            val shape: Shape = object : Shape() {
                override fun draw(canvas: Canvas, paint: Paint) {
                    canvas.drawRect(
                        plusOffset,
                        iconHalfSize - plusHalfStroke,
                        iconSize - plusOffset,
                        iconHalfSize + plusHalfStroke,
                        paint
                    )
                    canvas.drawRect(
                        iconHalfSize - plusHalfStroke,
                        plusOffset,
                        iconHalfSize + plusHalfStroke,
                        iconSize - plusOffset,
                        paint
                    )
                }
            }
            val drawable = ShapeDrawable(shape)
            val paint: Paint = drawable.paint
            paint.setColor(mPlusColor)
            paint.setStyle(Paint.Style.FILL)
            paint.setAntiAlias(true)
            return drawable
        }
}