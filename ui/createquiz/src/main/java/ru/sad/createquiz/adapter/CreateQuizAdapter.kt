package ru.sad.createquiz.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.sad.base.base.BaseFragment
import java.io.Serializable

class CreateQuizAdapter(
    private val fragmentManager: Fragment
) : Serializable, FragmentStateAdapter(fragmentManager) {

    val fragments = ArrayList<BaseFragment<*>>()

    override fun createFragment(position: Int): Fragment =
        fragments[position]

    override fun getItemCount(): Int = fragments.size

    fun addFragment(vararg fragments: BaseFragment<*>) {
        this.fragments.addAll(fragments.toList())
        notifyItemRangeChanged(this.fragments.size - fragments.size, fragments.size)
    }
}