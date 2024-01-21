package ru.sad.base.ext

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import ru.sad.base.base.BaseItem
import ru.sad.utils.toDp
import java.lang.Float.max


infix fun ImageView.tint(color: Int) {
    setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
}

fun RecyclerView.initVertical(
    groupAdapter: GroupAdapter<GroupieViewHolder>,
    recyclerOptions: RecyclerView.() -> Unit = {}
) {
    recyclerOptions(this)
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    adapter = groupAdapter
    disableBlink()
}

fun RecyclerView.initHorizontal(
    groupAdapter: GroupAdapter<GroupieViewHolder>,
    recyclerOptions: RecyclerView.() -> Unit
) {
    recyclerOptions(this)
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    adapter = groupAdapter
}


fun GroupAdapter<GroupieViewHolder>.addAll(vararg items: BaseItem) {
    this.updateAsync(items.toList())
}

infix fun TabLayout.attach(viewPager2: ViewPager2) {
    TabLayoutMediator(this, viewPager2) { page, pos ->
    }.attach()
}

fun RecyclerView.disableBlink() {
    (this.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
}

fun AppBarLayout.applyScrollOffsetToolbar(
    image: ShapeableImageView,
    title: TextView,
    startYTitle: Int,
    percentResize: Float = 0.3f,
    finalY: Float
) {
    var offsetInPercent = 0f
    val imageRange: Int = image.left - 16.toDp.toInt()
    val delay = 0.25f
    val speed = 2f
    val titleRange: Int = startYTitle
    val titleHeight = title.measuredHeight
    val maxHeight = -titleRange.toFloat() + titleHeight + (finalY / 2)

    addOnOffsetChangedListener { appBar, offset ->
        offsetInPercent = kotlin.math.abs(offset).toFloat() / appBar.totalScrollRange.toFloat()

        offsetInPercent = max(offsetInPercent - delay, 0f) * speed

        image.translationX = (offsetInPercent * imageRange * -1)
        image.scaleX = max(1.0f - offsetInPercent, percentResize)
        image.scaleY = max(1.0f - offsetInPercent, percentResize)

        title.translationY =
            max((offsetInPercent * titleRange * -1), maxHeight)
    }
}

fun AppBarLayout.applyAlphaByScrollOffsetWithCustomToolbar(
    title: ShapeableImageView,
    description: TextView,
    maxWidth: Float,
    percentOfTitleSize: Float = 0.2f,
    finalY: Float
) {
    val startX = title.x
    val startWidth = title.measuredWidth.toFloat()
    val startHeight = title.measuredHeight.toFloat()

    val startY = title.y

    val finalWidth = startWidth * percentOfTitleSize
    val finalHeight = startHeight * percentOfTitleSize

    addOnOffsetChangedListener { appBar, offset ->
        val offsetInPercent = offset.toFloat() / appBar.totalScrollRange.toFloat()

        description.alpha = 1.0f - kotlin.math.abs(offsetInPercent)
        val finalX = maxWidth / 2 - (title.width) / 2

        title.x = startX + (finalX - startX) * kotlin.math.abs(offsetInPercent)
        title.y = startY - (startY - finalY) * kotlin.math.abs(offsetInPercent)

        title.layoutParams.apply {
            width =
                (startWidth - (startWidth - finalWidth) * kotlin.math.abs(offsetInPercent)).toInt()
            height =
                (startHeight - (startHeight - finalHeight) * kotlin.math.abs(offsetInPercent)).toInt()
        }
    }
}

fun View.smoothShow(duration: Long = 500) {
    this@smoothShow.alpha = 0f
    this@smoothShow.visibility = View.VISIBLE
    this.animate()
        .alpha(1.0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                this@smoothShow.visibility = View.VISIBLE
            }
        })
}

fun View.smoothHide(duration: Long = 500) {
    this.animate()
        .alpha(0.0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@smoothHide.visibility = View.GONE
            }
        })
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    })
}

inline fun View.setOnClickMany(vararg items: View, crossinline onClick: () -> Unit) {
    items.forEach { it.setOnClickListener { onClick.invoke() } }
}

// Поддерживаемые MutableLiveData<State<List<*>>>, MutableLiveData<State<Pair<*, List<*>>>>
fun <T> NestedScrollView.paginationEvents(
    dataLive: MutableLiveData<State<T>>,
    call: (Int) -> Unit
) {
    var currentPage = 0

    setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
        val view = v.getChildAt(0)
        val diff: Int = view.bottom - (v.height + v.scrollY)

        if (diff == 0) {
            val isLoading = dataLive.value == State.Loading<T>()

            var dataIsEmpty = true

            // mazahizm
            if (dataLive.value is State.Success<T>) {
                if ((dataLive.value as State.Success<T>).value is List<*>) {
                    dataIsEmpty = ((dataLive.value as State.Success<T>).value as List<*>).isEmpty()
                } else if ((dataLive.value as State.Success<T>).value is Pair<*, *> && ((dataLive.value as State.Success<T>).value as Pair<*, *>).second is List<*>) {
                    dataIsEmpty =
                        (((dataLive.value as State.Success<T>).value as Pair<*, *>).second as List<*>).isEmpty()
                }
            }

            if (!isLoading && !dataIsEmpty) {
                currentPage++
                call.invoke(currentPage)
            }
        }
    })
}
