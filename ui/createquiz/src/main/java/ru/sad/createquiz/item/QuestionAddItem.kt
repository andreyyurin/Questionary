package ru.sad.createquiz.item

import android.view.View
import ru.sad.base.base.BaseItem
import ru.sad.createquiz.R

data class QuestionAddItem(val addItem: () -> Unit): BaseItem() {
    override fun getLayout(): Int = R.layout.item_question_add

    override fun View.bindView(position: Int) {
        setOnClickListener { addItem.invoke() }
    }
}