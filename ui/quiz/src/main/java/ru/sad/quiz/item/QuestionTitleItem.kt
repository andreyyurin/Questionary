package ru.sad.quiz.item

import android.view.View
import android.widget.TextView
import ru.sad.base.base.BaseItem
import ru.sad.quiz.R

data class QuestionTitleItem(private val title: String) : BaseItem() {

    private lateinit var tvQuestionTitle: TextView

    override fun getLayout(): Int = R.layout.item_quiz_question_title

    override fun View.bindView(position: Int) {
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle)
        tvQuestionTitle.text = title
    }
}