package ru.sad.subscriptions.dialog

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import ru.sad.base.base.BaseFragment
import ru.sad.base.ext.loadUserPhoto
import ru.sad.subscriptions.R

fun BaseFragment<*>.createProfileDialog(userId: Int?, name: String, openProfile: (Int) -> Unit) {
    context?.let {
        val dialog = AlertDialog.Builder(it).create()

        dialog.setView(
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_profile, null)
        )

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        dialog.findViewById<ShapeableImageView>(R.id.ivProfile).loadUserPhoto(userId)
        dialog.findViewById<TextView>(R.id.tvProfile).text = name

        dialog.findViewById<TextView>(R.id.btnOpenProfile).setOnClickListener {
            userId?.let(openProfile)
            dialog.dismiss()
        }

        dialog.findViewById<TextView>(R.id.tvProfile).viewTreeObserver.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    with(dialog.findViewById<ShapeableImageView>(R.id.ivProfile)) {
                        val mHeight = this.measuredHeight
                        val mWidth = this.measuredWidth

                        if (mHeight > mWidth) {
                            this.layoutParams.apply {
                                width = mHeight
                            }
                        } else {
                            this.layoutParams.apply {
                                height = mWidth
                            }
                        }
                    }

                    dialog.findViewById<TextView>(R.id.tvProfile).viewTreeObserver.removeOnGlobalLayoutListener(
                        this
                    )
                }
            })
    }
}