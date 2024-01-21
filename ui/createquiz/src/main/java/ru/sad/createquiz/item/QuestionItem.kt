package ru.sad.createquiz.item

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import ru.sad.base.base.BaseItem
import ru.sad.createquiz.R
import ru.sad.domain.model.quiz.QuizAnswer
import ru.sad.domain.model.quiz.QuizQuestion

class QuestionItem(
    var currentPosition: Int,
    private val clickSettings: (QuestionItem, View, Int) -> Unit,
    private val quizAnswer: (String, Int) -> Unit,
) : BaseItem() {

    private lateinit var layoutQuestion: ConstraintLayout
    private lateinit var ivSettings: ImageView
    private lateinit var etQuestion: EditText

    var isSelectedTrue = false

    override fun getLayout(): Int = R.layout.item_question

    override fun View.bindView(position: Int) {
        layoutQuestion = findViewById(R.id.layoutEtQuestion)
        ivSettings = findViewById(R.id.ivDots)
        etQuestion = findViewById(R.id.etQuestion)

        etQuestion.requestFocus()

        ivSettings.setOnClickListener {
            clickSettings.invoke(this@QuestionItem, ivSettings, currentPosition)
        }

        setupWatcher()
    }

    fun selectTrue() {
        if (!this::layoutQuestion.isInitialized) return

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

        layoutQuestion.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                layoutQuestion.context,
                ru.sad.base.R.drawable.bg_default_edittext
            )
        )
        isSelectedTrue = false
    }

    fun clear() {
        if (!this::layoutQuestion.isInitialized || !this::etQuestion.isInitialized) return
        unselectTrue()
        etQuestion.setText("")
    }

    fun getAnswer(): QuizAnswer? {
        if (!this::layoutQuestion.isInitialized || !this::etQuestion.isInitialized) return null
        return QuizAnswer(isSelectedTrue, etQuestion.text.toString())
    }

    private fun setupWatcher() {
        if (!this::layoutQuestion.isInitialized || !this::etQuestion.isInitialized) return

        etQuestion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                quizAnswer.invoke(s.toString(), currentPosition)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }
}