package ru.sad.channel.item

import android.view.View
import android.widget.TextView
import ru.sad.base.base.BaseItem
import ru.sad.channel.R

class ItemMessage(private val message: String) : BaseItem() {
    override fun getLayout(): Int = R.layout.item_message

    override fun View.bindView(position: Int) {
        findViewById<TextView>(R.id.tvMessage).text = message
    }
}