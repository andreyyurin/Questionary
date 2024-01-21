package ru.sad.topquiz.item

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupieViewHolder
import ru.sad.base.base.BaseItem
import ru.sad.base.ext.toPx
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.topquiz.R

data class FilterItem(
    private val titleIfEmpty: String,
    private val quiz: LiveData<String?>?
) : BaseItem() {

    override fun getLayout(): Int = R.layout.item_filter

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)

        with(viewHolder.root) {
            if (position == 0) {
                (layoutParams as RecyclerView.LayoutParams).apply {
                    marginStart = 0
                }
            } else {
                (layoutParams as RecyclerView.LayoutParams).apply {
                    marginStart = 16.toPx.toInt()
                }
            }

            findViewById<TextView>(R.id.tvFilter).text = titleIfEmpty

            quiz?.observeForever {
                findViewById<TextView>(R.id.tvFilter).text = it
            }
        }
    }
}