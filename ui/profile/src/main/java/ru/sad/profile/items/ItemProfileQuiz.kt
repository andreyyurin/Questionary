package ru.sad.profile.items

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.load
import ru.sad.base.ext.loadQuizImage
import ru.sad.domain.model.quiz.QuizShortResponse
import ru.sad.profile.R

data class ItemProfileQuiz(val quizShortResponse: QuizShortResponse) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_profile_quiz

    override fun View.bindView(position: Int) {
        val image = findViewById<ImageView>(R.id.ivItemPhoto)
        image?.loadQuizImage(quizShortResponse.id)

        if (quizShortResponse.rating < 4.0f) {
            findViewById<CardView>(R.id.cardRating).isGone = true
        } else {
            findViewById<CardView>(R.id.cardRating).isVisible = true
        }

        findViewById<TextView>(R.id.tvRating).text = quizShortResponse.rating.toString().format("%,.1f")
    }
}