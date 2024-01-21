package ru.sad.quiz.item

import android.view.View
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.setOnClickMany
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.quiz.R

class QuestionItem(
    private val answer: QuizAnswer,
    val index: Int,
    private val onClick: (QuestionItem) -> Unit
) : BaseItem() {

    private lateinit var layoutQuestion: ConstraintLayout
    private lateinit var tvQuestion: TextView

    private var isSelectedTrue = false

    override fun getLayout(): Int = R.layout.item_quiz_question

    override fun View.bindView(position: Int) {
        layoutQuestion = findViewById(R.id.layoutTvQuestion)
        tvQuestion = findViewById(R.id.tvQuestion)

        tvQuestion.text = answer.answer

        setOnClickMany(layoutQuestion, tvQuestion, onClick = {
            onClick.invoke(this@QuestionItem)
        })
    }

    fun selectTrue() {
        if (!this::layoutQuestion.isInitialized) return
        if (isSelectedTrue) return

        layoutQuestion.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                layoutQuestion.context,
                R.drawable.bg_true_question
            )
        )
        isSelectedTrue = true
    }

    fun unselectTrue() {
        if (!this::layoutQuestion.isInitialized) return
        if (!isSelectedTrue) return

        layoutQuestion.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                layoutQuestion.context,
                ru.sad.base.R.drawable.bg_default_edittext
            )
        )
        isSelectedTrue = false
    }

    fun setError() {
        if (!this::layoutQuestion.isInitialized) return

        layoutQuestion.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                layoutQuestion.context,
                R.drawable.bg_false_question
            )
        )
        isSelectedTrue = false
    }

    fun removeAllListeners() {
        if (!this::layoutQuestion.isInitialized) return

        layoutQuestion.setOnClickMany(layoutQuestion, tvQuestion, onClick = {

        })
    }
}