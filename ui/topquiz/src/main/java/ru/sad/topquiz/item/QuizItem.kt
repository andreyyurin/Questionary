package ru.sad.topquiz.item

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
import ru.sad.topquiz.R
import java.text.DecimalFormat

data class QuizItem(val quiz: QuizShortResponse) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_quiz

    override fun View.bindView(position: Int) {
        val image = findViewById<ImageView>(R.id.ivItemPhoto)
        val title = findViewById<TextView>(R.id.tvTitle)

        image?.loadQuizImage(quiz.id)
        title?.text = quiz.title

        findViewById<TextView>(R.id.tvAuthorName)?.text = quiz.authorName ?: ""

        if (quiz.rating < 4.0f) {
            findViewById<CardView>(R.id.cardRating).isGone = true
        } else {
            findViewById<CardView>(R.id.cardRating).isVisible = true
        }

        findViewById<TextView>(R.id.tvRating).text = quiz.rating.toString().format("%,.1f")
    }
}