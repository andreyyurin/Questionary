package ru.sad.photoviewer.item

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import ru.sad.base.base.BaseItem
import ru.sad.photoviewer.R

data class ItemColor(
    @ColorInt val mainColor: Int,
    val selectColorListener: ((Int, ItemColor) -> Unit)
) : BaseItem() {

    private var isLayoutSelected = false
    private var mainLayout: ConstraintLayout? = null


    override fun getLayout(): Int = R.layout.item_color

    override fun View.bindView(position: Int) {
        mainLayout = findViewById(R.id.mainLayout)

        mainLayout?.background =
            if (isLayoutSelected) mainLayout?.context?.getDrawable(R.drawable.bg_item_layout)
            else null

        findViewById<View>(R.id.viewColor).background = drawCircle(mainColor)

        mainLayout?.setOnClickListener { selectColorListener.invoke(mainColor, this@ItemColor) }
    }

    private fun drawCircle(backgroundColor: Int): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        shape.setColor(backgroundColor)
        return shape
    }

    fun select(isSelect: Boolean) {
        mainLayout?.setBackgroundDrawable(
            if (isSelect) mainLayout?.context?.getDrawable(R.drawable.bg_item_layout)
            else null
        )
        isLayoutSelected = isSelect
    }
}