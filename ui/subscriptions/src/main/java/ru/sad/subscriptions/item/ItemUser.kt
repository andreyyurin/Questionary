package ru.sad.subscriptions.item

import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.imageview.ShapeableImageView
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.loadUserPhoto
import ru.sad.domain.model.users.User
import ru.sad.subscriptions.R

data class ItemUser(
    val user: User,
    val onFollowClick: (ItemUser) -> Unit,
    val onUnFollowClick: (ItemUser) -> Unit
) : BaseItem() {

    private lateinit var btnFollow: TextView
    private lateinit var viewLoading: FrameLayout
    override fun getLayout(): Int = R.layout.item_user

    override fun View.bindView(position: Int) {
        findViewById<TextView>(R.id.tvUsername).text = user.username
        findViewById<ShapeableImageView>(R.id.ivUserPhoto).loadUserPhoto(user.id)

        btnFollow = findViewById(R.id.btnFollow)
        viewLoading = findViewById(R.id.viewLoading)

        updateText()
    }

    fun updateText() {
        if (!this::btnFollow.isInitialized) return

        btnFollow.isVisible = true
        viewLoading.isGone = true

        if (user.subscribed == true) {
            btnFollow.text =
                btnFollow.context.getString(R.string.subscription_seacrh_btn_unfollow)

            btnFollow.setBackgroundResource(R.drawable.bg_btn_unfollow)
        } else {
            btnFollow.text =
                btnFollow.context.getString(R.string.subscription_seacrh_btn_follow)

            btnFollow.setBackgroundResource(R.drawable.bg_btn_follow)
        }

        btnFollow.setOnClickListener {
            if (user.subscribed == true) {
                onUnFollowClick.invoke(this)
            } else {
                onFollowClick.invoke(this)
            }
        }
    }

    fun loading() {
        if (!this::viewLoading.isInitialized) return

        btnFollow.isInvisible = true
        viewLoading.isVisible = true
        btnFollow.setOnClickListener(null)
    }
}