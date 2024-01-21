package ru.sad.profile.items

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.load
import ru.sad.profile.R

data class ItemProfilePhoto(val imageUri: Uri) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_profile_photo

    override fun View.bindView(position: Int) {
        val image = findViewById<ImageView>(R.id.ivItemPhoto)
        image?.load(imageUri.toString())
    }
}