package ru.sad.createquiz.item

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import ru.sad.base.base.BaseItem
import ru.sad.createquiz.R

class QuestionTitleItem(var titleUpdate: (String) -> Unit) : BaseItem() {

    private lateinit var etQuestionTitle: EditText

    override fun getLayout(): Int = R.layout.item_question_title

    override fun View.bindView(position: Int) {
        etQuestionTitle = findViewById(R.id.etQuestionTitle)
        setupWatcher()
    }

    private fun setupWatcher() {
        if (!this::etQuestionTitle.isInitialized) return

        etQuestionTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                titleUpdate.invoke(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

//    fun getTitle(): String? {
//        if (!this::etQuestionTitle.isInitialized) return null
//        return etQuestionTitle.text.toString()
//    }
}