package ru.sad.base.base

import android.view.View
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.UUID
import kotlin.math.absoluteValue

abstract class BaseItem(private val id: Long? = null) : Item<GroupieViewHolder>() {

    private val uniqueId = generateUniqueId().absoluteValue

    var itemClicks: (Int) -> Unit = {}

    var itemLongClicks: (Int) -> Boolean = { true }

    override fun getId(): Long = id ?: uniqueId

    open fun View.bindView(position: Int) {}

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.root.setOnClickListener { itemClicks.invoke(position) }
        viewHolder.root.setOnLongClickListener { itemLongClicks.invoke(position) }
        viewHolder.root.bindView(position)
    }

    private fun generateUniqueId(): Long = UUID.randomUUID().toString().generateUniqueId()

    fun String.generateUniqueId(): Long {
        var result = -0x340d631b7bdddcdbL
        val len = this.length
        for (i in 0 until len) {
            result = result xor this[i].toLong()
            result *= 0x100000001b3L
        }
        return result
    }

}
