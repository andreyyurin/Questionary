package ru.sad.createquiz.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.ListPopupWindow

class CustomSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSpinner(context, attrs, defStyleAttr) {

    init {
    }

    override fun getWindowVisibleDisplayFrame(outRect: Rect) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        display.getRectSize(outRect);
        outRect.set(outRect.left, 100, outRect.right, outRect.bottom);
        super.getWindowVisibleDisplayFrame(outRect)
    }


    fun AppCompatSpinner.avoidDropdownFocus() {
        try {
            val isAppCompat = this is androidx.appcompat.widget.AppCompatSpinner
            val spinnerClass = if (isAppCompat) androidx.appcompat.widget.AppCompatSpinner::class.java else Spinner::class.java
            val popupWindowClass = if (isAppCompat) androidx.appcompat.widget.ListPopupWindow::class.java else android.widget.ListPopupWindow::class.java

            val listPopup = spinnerClass
                .getDeclaredField("mPopup")
                .apply { isAccessible = true }
                .get(this)
            if (popupWindowClass.isInstance(listPopup)) {
                val popup = popupWindowClass
                    .getDeclaredField("mPopup")
                    .apply { isAccessible = true }
                    .get(listPopup)
                if (popup is PopupWindow) {
                    popup.isFocusable = false
                    popup.height = 100
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getFitsSystemWindows(): Boolean = true
}