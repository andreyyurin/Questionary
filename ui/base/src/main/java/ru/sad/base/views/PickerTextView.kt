package ru.sad.base.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginTop
import com.google.android.material.imageview.ShapeableImageView
import ru.sad.base.R
import ru.sad.base.ext.toPx
import ru.sad.base.ext.toSp
import ru.sad.utils.toDp

class PickerTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val click: () -> Unit,
    private val text: String,
    private val isFirstElement: Boolean = false,
    private val textSize: Int = 17
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        isDrawingCacheEnabled = true
        init(text, click, isFirstElement, textSize)
    }

    private fun init(
        text: String,
        onClick: () -> Unit,
        isFirstElement: Boolean = false,
        textSize: Int
    ) {
        inflate(context, R.layout.view_picker_text, this)

        measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        layout(0, 0, measuredWidth, measuredHeight)

        with(findViewById<TextView>(R.id.tvPicker)) {
            this.text = text
            this.setOnClickListener {
                onClick.invoke()
            }
            this.textSize = textSize.toDp
            this.layoutParams.apply {
                if (isFirstElement) (this as LayoutParams).setMargins(0, 0, 0, 0)
            }
        }

        buildDrawingCache(true)
    }
}