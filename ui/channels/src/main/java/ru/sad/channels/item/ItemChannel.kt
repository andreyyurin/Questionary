package ru.sad.channels.item

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.imageview.ShapeableImageView
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.loadUserPhoto
import ru.sad.channels.R
import ru.sad.domain.model.channels.ChannelShortResponse
import ru.sad.domain.model.users.User

data class ItemChannel(
    val channel: ChannelShortResponse
) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_channel

    override fun View.bindView(position: Int) {
        findViewById<TextView>(R.id.tvChannelName).text = channel.name
    }
}