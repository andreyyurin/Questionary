package ru.sad.profile.items

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import ru.sad.base.base.BaseItem
import ru.sad.profile.R

data class ItemProfileVideo(val imageBitmap: Bitmap) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_profile_video

    override fun View.bindView(position: Int) {
        val image = findViewById<ImageView>(R.id.ivItemVideo)
        image?.setImageBitmap(imageBitmap)
    }
}