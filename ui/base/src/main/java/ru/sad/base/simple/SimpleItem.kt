package ru.sad.base.simple

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.sad.base.R
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.load

data class SimpleItem(private val data: SimpleListBottomSheet.SimpleData) : BaseItem() {
    override fun getLayout(): Int = R.layout.item_simple

    override fun View.bindView(position: Int) {
        findViewById<TextView>(R.id.tvSimple).text = data.title

        with(findViewById<ImageView>(R.id.ivSimple)) {
            if (data.image.isNullOrEmpty()) {
                this.isGone = true
            } else {
                this.isVisible = true
            }
            this.load(data.image)
        }
    }
}