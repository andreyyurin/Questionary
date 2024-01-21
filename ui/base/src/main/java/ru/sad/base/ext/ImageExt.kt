package ru.sad.base.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.RawRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import ru.sad.base.BuildConfig
import ru.sad.base.R
import ru.sad.data.prefs.AuthPref.authToken


fun ImageView.load(url: String?) {
    Glide
        .with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadGif(@RawRes res: Int) {
    Glide
        .with(this)
        .asGif()
        .load(res)
        .into(object : ImageViewTarget<GifDrawable>(this) {
            override fun setResource(resource: GifDrawable?) {
                this@loadGif.setImageDrawable(resource)
            }
        })
}

fun ShapeableImageView.load(url: String?) {
    Glide
        .with(this)
        .load(url)
        .into(this)
}

fun ImageView.loadUserPhoto(userId: Int?) {
    val glideUrl = GlideUrl(
        BuildConfig.QUESTIONARY_API_ENDPOINT + "users/image?userId=$userId",
        LazyHeaders.Builder()
            .addHeader("token", authToken.toString())
            .build()
    )

    Glide
        .with(this)
        .load(glideUrl)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                this@loadUserPhoto.setImageResource(R.drawable.ic_photo)
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                this@loadUserPhoto.setImageDrawable(resource)
                return true
            }
        })
        .into(this)
}

fun ImageView.loadQuizImage(id: Int?) {
    val glideUrl = GlideUrl(
        BuildConfig.QUESTIONARY_API_ENDPOINT + "quiz/get-quiz-image?id=$id",
        LazyHeaders.Builder()
            .addHeader("token", authToken.toString())
            .build()
    )

    Glide
        .with(this)
        .load(glideUrl)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                this@loadQuizImage.setImageResource(R.drawable.ic_photo)
                return true
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                this@loadQuizImage.setImageDrawable(resource)
                return true
            }
        })
        .into(this)
}

fun ImageView.loadStoryImage(userId: Int?) {
    val glideUrl = GlideUrl(
        BuildConfig.QUESTIONARY_API_ENDPOINT + "stories/image?userId=$userId",
        LazyHeaders.Builder()
            .addHeader("token", authToken.toString())
            .build()
    )
    Glide
        .with(this)
        .load(glideUrl)
        .into(this)
}