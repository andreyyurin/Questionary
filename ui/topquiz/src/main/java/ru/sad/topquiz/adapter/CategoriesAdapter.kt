package ru.sad.topquiz.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.sad.domain.model.quiz.QuizCategory
import ru.sad.topquiz.R

class CategoriesAdapter : BaseAdapter() {

    private val items = ArrayList<QuizCategory>()

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: CategoryViewHolder

        if (convertView == null) {
            view =
                LayoutInflater.from(parent?.context).inflate(R.layout.item_category, parent, false)
            viewHolder = CategoryViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as CategoryViewHolder
        }

        viewHolder.bind(items[position])

        return view
    }

    fun updateData(categories: List<QuizCategory>) {
        this.items.clear()
        this.items.addAll(categories)
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) {

        private val tvCategory = itemView.findViewById<TextView>(R.id.tvCategory)

        fun bind(category: QuizCategory) {
            tvCategory.text = category.name
        }
    }
}